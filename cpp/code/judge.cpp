#include <stdlib.h>
#include <iostream>
#include <string>
#include <cstring>
#include <fstream>
#include <sys/ptrace.h>
#include <sys/user.h>
#include <sys/wait.h>
#include <sys/resource.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <sys/reg.h>
#include <unistd.h>

#include "mysql_connection.h"
#include "cppconn/driver.h"
#include "cppconn/resultset.h"
#include "cppconn/prepared_statement.h"

#define MB 1048576
#define COMPILE_TIME 6
#define COMPILE_MEM 128
#define FILE_SIZE 10

#define LAN_C 1
#define LAN_CPP 2
#define LAN_JAVA 3
#define TOTAL_LAN 3

#define OJ_AC 1
#define OJ_WA 2
#define OJ_TLE 3
#define OJ_MLE 4
#define OJ_RE 5
#define OJ_OLE 6
#define OJ_CE 7
#define OJ_SE 8
#define OJ_QU 9
#define OJ_CL 10
#define OJ_RN 11

#define OFFSET_OLE 1024

// C and C++
const int ALLOW_SYS_CALL_C[] = {0,1,2,3,4,5,8,9,11,12,20,21,59,63,89,158,231,240, SYS_time, SYS_read, SYS_uname, SYS_write
	, SYS_open, SYS_close, SYS_execve, SYS_access, SYS_brk, SYS_munmap, SYS_mprotect, SYS_mmap, SYS_fstat
	, SYS_set_thread_area, 252, SYS_arch_prctl, 0 };
// java
const int ALLOW_SYS_CALL_JAVA[] = { 0,2,3,4,5,9,10,11,12,13,14,21,56,59,89,97,104,158,202,218,231,273,257,
		61, 22, 6, 33, 8, 13, 16, 111, 110, 39, 79, SYS_fcntl, SYS_getdents64, SYS_getrlimit, SYS_rt_sigprocmask
		, SYS_futex, SYS_read, SYS_mmap, SYS_stat, SYS_open, SYS_close, SYS_execve, SYS_access, SYS_brk, SYS_readlink
		, SYS_munmap, SYS_close, SYS_uname, SYS_clone, SYS_uname, SYS_mprotect, SYS_rt_sigaction, SYS_getrlimit
		, SYS_fstat, SYS_getuid, SYS_getgid, SYS_geteuid, SYS_getegid, SYS_set_thread_area, SYS_set_tid_address
		, SYS_set_robust_list, SYS_exit_group, 158, 0 };

char* hostname = nullptr;
char* username = nullptr;
char* passwd = nullptr;
char* dbname = nullptr;

const char* MYSQL_ERROR_LOG = "mysqlError.log";
const char* SYSTEM_ERROR_LOG = "systemError.log";

bool allowSysCall[512];

int compile(int lang, const char* ceFile, const char* runId);
void updateCompileErrorInfo(const char* runId, const char* ceFile);
void updateSubmitStatus(const char* runId, int result, const char* ceInfo);
void updateSubmitStatus(const char* runId, int result, int time, int mem);
void judge(char* runId, char* cid, char* pNo, char* lang);
void saveFile(const char* data, const char* fileName);
int checkResult(std::string dataOut, const char* userOutFileName);
void saveErrorLog(const sql::SQLException& e);
void run(int lang, int timeLimit, int memLimit, int& usedTime, const char* dataIn
	, const char* userOut, const char* errOut);
void watchRunningStatus(pid_t pidRun, const char* errFile, int lang, int& result, int& topMem,
	int& usedTime, int memLimit, int timeLimit, const char* userOut, int outputLen);
int getProcStatus(int pid, const char* statusTItle);
int getPageFaultMem(const struct rusage& usage);
int getFileSize(const char* fileName);
void initAllowSysCall(int lang);

int main(int argc, char *argv[]) {
	// argv is command, runId, cid, pno, language, hostname username passwd dbname
    if (argc != 8+1) {
		printf("argv num error:%d\n",argc);
        return 1;
    }

//	freopen("debug.dat", "w", stdout);

	hostname = argv[5];
	username = argv[6];
	passwd = argv[7];
	dbname = argv[8];

	const char* ceFile = "ce.dat";
	int status = compile(atoi(argv[4]), ceFile, argv[1]);

	if (status == -1) {// system error
		updateSubmitStatus(argv[1], -1, 0, 0);
	} else if (status != 0) {// compile error
		updateCompileErrorInfo(argv[1], ceFile);
	} else {// compile success
		judge(argv[1], argv[2], argv[3], argv[4]);
	}

	return 0;
}

int compile(int lang, const char* ceFile, const char* runId){// return 0 means compile success
	updateSubmitStatus(runId, OJ_CL, nullptr);// set compiling

	int time_limit = COMPILE_TIME;
	int memory_limit = COMPILE_MEM*MB;

	// compile commands
	// c and c++ must use --static to compile, otherwise the memory statistics will be incorrect
	const char *COMP_C[] = {"gcc","-Wall","-lm", "--static","Main.c","-o","Main",nullptr};
	const char *COMP_CPP[] = {"g++","-Wall","-fno-asm","-lm", "--static", "-std=c++11"
		,"Main.cpp","-o","Main",nullptr};
	const char *COMP_JAVA[] = {"javac","-J-Xms32m","-J-Xmx64m","-encoding","UTF-8"
		,"Main.java",nullptr};

	pid_t pid = fork();
	if (pid == -1){
		// system error
		std::ofstream fout(SYSTEM_ERROR_LOG, std::ios_base::out | std::ios_base::app);
		fout << "Error: fork() file when compile.\n";
		fout << "In file " << __FILE__ << " function (" << __FUNCTION__ << ")"
			<<" , line: " << __LINE__ << "\n";
		fout.close();
		return -1;
	} else if (pid != 0) {// parent
        int status;
        waitpid(pid, &status, 0);

		return status;
	} else {// child
		/* set rlimit*/
		struct rlimit lim;
		lim.rlim_cur = lim.rlim_max = time_limit;
		setrlimit(RLIMIT_CPU, &lim);

		alarm(0);
		alarm(time_limit);

	//	lim.rlim_max =
		lim.rlim_cur = memory_limit;
		setrlimit(RLIMIT_AS, &lim);

		lim.rlim_cur = lim.rlim_max = FILE_SIZE*MB;
    	setrlimit(RLIMIT_FSIZE, &lim);

		/* start compile */
		freopen(ceFile,"w",stderr);
		switch(lang){
			case LAN_C: execvp(COMP_C[0],(char* const*)COMP_C); break;
			case LAN_CPP: execvp(COMP_CPP[0],(char* const*)COMP_CPP); break;
			case LAN_JAVA: execvp(COMP_JAVA[0],(char* const*)COMP_JAVA); break;
			default: std::cout << "Nothing to do.\n";
		}

		exit(0);
	}
}

void updateCompileErrorInfo(const char* runId, const char* ceFile){
	std::ifstream fin(ceFile);
	std::string ceStr = "";
	std::string line;
	while (getline(fin,line)) {
		ceStr += line;
	}
	fin.close();

	updateSubmitStatus(runId, OJ_CE, ceStr.c_str());
}

void updateSubmitStatus(const char* runId, int result, const char* ceInfo) {
	sql::Driver* driver = nullptr;
	sql::Connection* conn = nullptr;
	sql::PreparedStatement* ps = nullptr;

	try {
		driver = get_driver_instance();
		conn = driver->connect(hostname,username,passwd);
		conn->setSchema(dbname);

		const char* sql = "call updateSubmitResult(?,?,?)";
		ps = conn->prepareStatement(sql);

		ps->setString(1,runId);
		ps->setInt(2,result);
		if (nullptr == ceInfo) {
			const char* tmp = "";
			ps->setString(3,tmp);
		} else {
			ps->setString(3, ceInfo);
		}
		ps->execute();

		delete ps;
		delete conn;
	} catch (sql::SQLException& e) {
		saveErrorLog(e);
	}

}

void updateSubmitStatus(const char* runId, int result, int time, int mem) {
	sql::Driver* driver = nullptr;
	sql::Connection* conn = nullptr;
	sql::PreparedStatement* ps = nullptr;

	try {
		driver = get_driver_instance();
		conn = driver->connect(hostname,username,passwd);
		conn->setSchema(dbname);

		const char* sql = "call updateRunningResult(?,?,?,?)";
		ps = conn->prepareStatement(sql);

		ps->setString(1,runId);
		ps->setInt(2,result);
		ps->setInt(3,time);
		ps->setInt(4,mem);
		ps->execute();

		delete ps;
		delete conn;
	} catch (sql::SQLException& e) {
		saveErrorLog(e);
	}
}

void judge(char* runId, char* cid, char* pno, char* lang) {
	updateSubmitStatus(runId, OJ_RN, nullptr);// set running

	const char* dataIn = "data.in";
	const char* userOut = "user.out";
	const char* errOut = "err.out";

	/* get time limit and memory limit of this problem */
	int timeLimit = 1, memLimit = 64;
	try {
		sql::Driver* driver = get_driver_instance();
		sql::Connection* conn = driver->connect(hostname,username,passwd);
		conn->setSchema(dbname);

		const char* sql = "call showProblem(?,?)";
		sql::PreparedStatement* ps = conn->prepareStatement(sql);
		ps->setString(1,cid);
		ps->setString(2,pno);

		sql::ResultSet* rs = ps->executeQuery();
		if (rs->next()) {
			timeLimit = rs->getInt(3); // ms
			memLimit = rs->getInt(4);  // MB
		}

		delete rs;
		delete ps;
		delete conn;
	} catch (sql::SQLException& e) {
		saveErrorLog(e);
		updateSubmitStatus(runId, OJ_SE, 0, 0);
	}

	/* start judge */
	try {
		/* get all input data and output data of this problem */
		sql::Driver* driver = get_driver_instance();
		sql::Connection* conn = driver->connect(hostname,username,passwd);
		conn->setSchema(dbname);

		const char* sql = "call getInputOutputData(?,?)";
		sql::PreparedStatement* ps = conn->prepareStatement(sql);
		ps->setString(1,cid);
		ps->setString(2,pno);
		sql::ResultSet* rs = ps->executeQuery();

		int result = OJ_AC;
		int maxTime = 0;
		int topMem = 0;

		initAllowSysCall(atoi(lang));
		while (rs->next() && OJ_AC==result) {
			std::string inputData = rs->getString(1);
			std::string outputData = rs->getString(2);

			saveFile(inputData.c_str(), dataIn);

			int usedTime = 0;

			pid_t pidRun = fork();
			if (pidRun == 0) {
				// run the submit program
				run(atoi(lang), timeLimit, memLimit, usedTime, dataIn, userOut, errOut);
				exit(0);
			} else if (pidRun != -1) {
				// watch the running process, and update the result, max memory and time
				watchRunningStatus(pidRun, errOut, atoi(lang), result, topMem, usedTime
					, memLimit, timeLimit, userOut, outputData.length());
				if (maxTime < usedTime) maxTime = usedTime;
			} else {// system error
				std::ofstream fout(SYSTEM_ERROR_LOG, std::ios_base::out | std::ios_base::app);
				fout << "# ERR fork in " << __FILE__;
				fout << "function is (" << __FUNCTION__ << ") in line " << __LINE__ << "\n";
				fout.close();

				result = OJ_SE;
			}

			if (OJ_AC == result) {
				result = checkResult(outputData, userOut);
			}
		}

		delete rs;
		delete ps;
		delete conn;
		updateSubmitStatus(runId, result, maxTime, topMem>>20);
	}catch (sql::SQLException& e) {
		saveErrorLog(e);
		updateSubmitStatus(runId, OJ_SE, 0, 0);
	}
}

void saveFile(const char* data, const char* fileName) {
	std::ofstream fout(fileName);
	fout << data;
	fout.close();
}

int checkResult(std::string dataOut, const char* userOutFileName) {
	/* get user output data */
	std::ifstream fin(userOutFileName);
	std::string line;
	std::string userOut = "";

	while (getline(fin,line)) {
		userOut += line+"\n";
	}

	fin.close();

	/* compare */
	int dLen = dataOut.length();
	int uLen = userOut.length();

	int result = OJ_AC;
	if (uLen >= (dLen<<1)+OFFSET_OLE) result = OJ_OLE;	// output limit exceeded
	else if (uLen!=dLen || dataOut.compare(userOut)!=0) result = OJ_WA;

	return result;
}

void saveErrorLog(const sql::SQLException& e) {
	std::ofstream fout(MYSQL_ERROR_LOG, std::ios_base::out | std::ios_base::app);

	fout << "# ERR: SQLException in " << __FILE__;
	fout << "# ERR: " << e.what();
	fout << " (MySQL error code: " << e.getErrorCode();
	fout << ", SQLState: " << e.getSQLState() << " )" << "\n";

	fout.close();
}

void run(int lang, int timeLimit, int memLimit, int& usedTime, const char* dataIn
		, const char* userOut, const char* errOut) {
	freopen(dataIn, "r", stdin);
	freopen(userOut, "w", stdout);
	freopen(errOut, "w", stderr);

	ptrace(PTRACE_TRACEME, 0, nullptr, nullptr);

	// setrlimit
	struct rlimit lim;

	lim.rlim_cur = lim.rlim_max = timeLimit/1000.0+1;// the unit of timeLimit is ms
	setrlimit(RLIMIT_CPU, &lim);
	alarm(0);
	alarm(timeLimit/1000.0+1);

	lim.rlim_max = (FILE_SIZE<<20) + MB;// the unit of FILE_SIZE is MB
	lim.rlim_cur = FILE_SIZE<<20;
	setrlimit(RLIMIT_FSIZE, &lim);

	switch (lang) {
		case 3 : // java
			lim.rlim_cur = lim.rlim_max = 80; break;
		default :
			lim.rlim_cur = lim.rlim_max = 1;
	}
	setrlimit(RLIMIT_NPROC, &lim);

	lim.rlim_cur = lim.rlim_max = MB << 6;
	setrlimit(RLIMIT_STACK, &lim);

	if (lang < 3) {
		lim.rlim_cur = memLimit * MB / 2 * 3;// the unit of memLimit is MB
		lim.rlim_max = memLimit * MB;
		setrlimit(RLIMIT_AS, &lim);
	}

	switch (lang) {
		case 1: case 2:
			execl("./Main", "./Main", nullptr); break;
		case 3:
			char javaXms[32];
			sprintf(javaXms, "-Xmx%dM", memLimit);
			execl("/usr/bin/java", "/usr/bin/java", javaXms, "-Djava.security.manager"
				, "-Djava.security.policy=./java.policy", "Main", nullptr);
			break;
	}
}

void watchRunningStatus(pid_t pidRun, const char* errFile, int lang, int& result, int& topMem
		, int& usedTime, int memLimit, int timeLimit, const char* userOut, int outputLen) {
	// the unit of memLimit is MB, the unit of timeLimit is ms

	int tmpMem = 0;
	int status, sig, exitCode;
	struct rusage usage;

	if (topMem == 0)
		topMem = getProcStatus(pidRun, "VmRSS:")<<10;

	wait(&status);// wait execl
	if (WIFEXITED(status)) return;

	ptrace(PTRACE_SETOPTIONS, pidRun, nullptr
				, PTRACE_O_TRACESYSGOOD | PTRACE_O_TRACEEXIT | PTRACE_O_EXITKILL);
	ptrace(PTRACE_SYSCALL, pidRun, nullptr, nullptr);

//	topMem = getProcStatus(pidRun, "VmRSS:")<<10;

	while (true) {
		wait4(pidRun, &status, __WALL, &usage);

		/* update topMem and result */
		if (lang == 3) tmpMem = getPageFaultMem(usage);
		else tmpMem = getProcStatus(pidRun, "VmPeak:") << 10;	// the unit of function is KB

		if (tmpMem > topMem) topMem = tmpMem;
		if (topMem > (memLimit<<20)) {
			if (result == OJ_AC) result = OJ_MLE;
			ptrace(PTRACE_KILL, pidRun, nullptr, nullptr);

			break;
		}

		if (WIFEXITED(status)) break;
		if (getFileSize(errFile)!=0) {
//			std::cout << "RE in line " << __LINE__ << "\n";

			if (result == OJ_AC) result = OJ_RE;
			ptrace(PTRACE_KILL, pidRun, nullptr, nullptr);
			break;
		}

		if (getFileSize(userOut) > outputLen*2+OFFSET_OLE){
			if (result == OJ_AC) result = OJ_OLE;
			ptrace(PTRACE_KILL, pidRun, nullptr, nullptr);
			break;
		}

		exitCode = WEXITSTATUS(status);
		if ( !((lang==3 && exitCode==17) || exitCode==0 || exitCode==133 || exitCode==5) ) {
//			std::cout << "error in line " << __LINE__ << ", exitCode is " << exitCode << "\n";

			if (result == OJ_AC) {
				switch (exitCode) {
					case SIGCHLD :
					case SIGALRM :
						alarm(0);
					case SIGKILL :
					case SIGXCPU :
						result = OJ_TLE;
						break;
					case SIGXFSZ :
						result = OJ_OLE;
						break;
					default :
						result = OJ_RE;
				}
			}
			ptrace(PTRACE_KILL, pidRun, nullptr, nullptr);
			break;
		}

		if (WIFSIGNALED(status)) {
			sig = WTERMSIG(status);

//			std::cout << "error in line " << __LINE__ << ", signal is " << sig << "\n";

			if (result == OJ_AC) {
				switch (sig) {
					case SIGCHLD :
					case SIGALRM :
						alarm(0);
					case SIGKILL :
					case SIGXCPU :
						result = OJ_TLE;
						break;
					case SIGXFSZ :
						result = OJ_OLE;
						break;
					default :
						result = OJ_RE;
				}
			}
			ptrace(PTRACE_KILL, pidRun, nullptr, nullptr);
			break;
		}

		// check invalid system call, x64 is ORIG_RAX*8, x86 is ORIG_EAX*4
		int sysCall = ptrace(PTRACE_PEEKUSER, pidRun, ORIG_RAX<<3, nullptr);
		if (!allowSysCall[sysCall]) {
//			std::cout << "error in line " << __LINE__ << ", system call id is " << sysCall << "\n";

			result = OJ_RE;
			ptrace(PTRACE_KILL, pidRun, nullptr, nullptr);
			break;
		}

		ptrace(PTRACE_SYSCALL, pidRun, nullptr, nullptr);
	}

	if (result == OJ_TLE) usedTime = timeLimit;
	else {
		usedTime += (usage.ru_utime.tv_sec*1000 + usage.ru_utime.tv_usec/1000);
		usedTime += (usage.ru_stime.tv_sec*1000 + usage.ru_stime.tv_usec/1000);
	}
}

int getProcStatus(int pid, const char* statusTitle) {// get memory info from /proce/pid/status
	char file[64];
	sprintf(file, "/proc/%d/status", pid);
	std::ifstream fin(file);
	std::string line;

	int sLen = strlen(statusTitle);

	int status = 0;// the unit of status is KB
	while (getline(fin, line)) {
//		std::cout << line << "\n";

		int lLen = line.length();
		if (lLen <= sLen) continue;

		// find line
		bool flag = true;
		for (int i=0; i<sLen; ++i) {
			if (line[i] != statusTitle[i]) {
				flag = false;
				break;
			}
		}

		if (flag) {
			// get status
			for (int i=sLen; i<lLen; ++i){
				if (line[i]>='0' && line[i]<='9') status = status*10 + line[i]-'0';
				else if (status) break;
			}
			break;
		}
	}

//	std::cout << "\n";

	return status;
}

int getPageFaultMem(const struct rusage& usage) {// get the memory of java program
	return usage.ru_minflt * getpagesize();
}

int getFileSize(const char* fileName) {
	struct stat f_stat;

	if (stat(fileName, &f_stat) == -1) return 0;
	else return f_stat.st_size;
}

void initAllowSysCall(int lang) {
	memset(allowSysCall, false ,sizeof(allowSysCall));

	switch (lang) {
		case 1: case 2:
			for (int i=0; !i || ALLOW_SYS_CALL_C[i]; ++i) {
				allowSysCall[ALLOW_SYS_CALL_C[i]] = true;
			}
			break;
		case 3:
			for (int i=0; !i || ALLOW_SYS_CALL_JAVA[i]; ++i) {
				allowSysCall[ALLOW_SYS_CALL_JAVA[i]] = true;
			}
			break;
	}
}
