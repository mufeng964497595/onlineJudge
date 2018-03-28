#include <cstdio>
#include <iostream>
#include <unistd.h>
#include <sys/wait.h>
#include <string>
#include <cstring>
#include <signal.h>
#include <stdlib.h>
#include <sys/resource.h>
#include <fstream>
#include <queue>

#include "mysql_connection.h"
#include "cppconn/driver.h"
#include "cppconn/exception.h"
#include "cppconn/resultset.h"
#include "cppconn/prepared_statement.h"

struct Submission {
	std::string runId;
	std::string cid;
	std::string pno;
	std::string lang;
	std::string code;

	Submission(){}
	Submission(std::string _runId, std::string _cid, std::string _pno, std::string _lang,
			std::string _code) {
		runId = _runId;	cid = _cid;	pno = _pno; lang = _lang; code = _code;
	}
};

std::queue<Submission> submissionQueue;

bool writeCodeIntoFile(const char* workdir, int lang,const char* code);

static const char* CODE_FILE[] = {"", "Main.c", "Main.cpp", "Main.java"};

int main(int argc, char* argv[]){
	freopen("polling.dat","a+",stdout);

	// argv[] is command hostname username passwd dbname judge_shell workdir
	if (argc != 6+1) {
		std::cout << "argv num eror: " << argc << "\n";
		return 1;
	}

	const int QUEUEING = 9;// queueing status code
	
	sql::Driver* driver = nullptr;
	sql::Connection* conn = nullptr;
	sql::PreparedStatement* ps = nullptr;
	sql::ResultSet* rs = nullptr;
	try {
		driver = get_driver_instance();

		while (true) {
			/* select all submissions which is in queueing order by runId*/
			conn = driver->connect(argv[1],argv[2],argv[3]);
			conn->setSchema(argv[4]);
			
			const char* sql = "call getQueueingSubmit(?)";
			ps = conn->prepareStatement(sql);
			ps->setInt(1,QUEUEING);
			rs = ps->executeQuery();

			while (rs->next()){
				std::string runId = rs->getString(1);
				std::string cid = rs->getString(2);
				std::string pno = rs->getString(3);
				std::string lang = rs->getString(4);
				std::string code = rs->getString(5);
			
				Submission submission(runId, cid, pno, lang, code);
				submissionQueue.push(submission);
			}

			delete rs;
			delete ps;
			delete conn;

			/* prepared to judge */
			while (!submissionQueue.empty()) {
				Submission s = submissionQueue.front();	submissionQueue.pop();	

				if (!writeCodeIntoFile(argv[6],atoi(s.lang.c_str()),s.code.c_str())) {
					std::cout << "Write code into file error!\n";
					return -1;
				}

				pid_t pid = fork();
				if (0==pid) {// child
					// use judge_shell to judge this submission
					execl(argv[5],argv[5],argv[6],(char*)s.runId.c_str(),(char*)s.cid.c_str()
						,(char*)s.pno.c_str(),(char*)s.lang.c_str()
						,argv[1],argv[2],argv[3],argv[4],nullptr);
					exit(0);
				} else if (-1!=pid) {// parent
					waitpid(pid,nullptr,0);
				} else {
					std::cout << "Fork error!\n";
					return -1;
				}
			}
			
			sleep(1);
		}
	} catch (sql::SQLException& e) {
		std::cout << "# ERR: SQLException in " << __FILE__;
		std::cout << "(" << __FUNCTION__ << ") on line " << __LINE__ << "\n";
		std::cout << "# ERR: " << e.what();
		std::cout << " (MySQL error code: " << e.getErrorCode();
		std::cout << ", SQLState: " << e.getSQLState() << " )" << "\n";
	}

	return 0;
}

bool writeCodeIntoFile(const char* workdir, int lang, const char* code){
	if (lang<1 || lang>=(int)(sizeof(CODE_FILE)/sizeof(CODE_FILE[0]))) {
		std::cout << "Code language error!\n";
		return false;
	}

	char wd[1024];
	sprintf(wd,"%s/%s",workdir,CODE_FILE[lang]);

	std::ofstream fout(wd);
	fout << code;
	fout.close();

	return true;
}
