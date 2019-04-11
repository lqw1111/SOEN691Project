import writeCSV

class fileInfo(object):
    def __init__(self):
        self.num_changes = 0
        self.code_churn = 0
        self.num_authors = 0
        self.unique_authors = set()

    def get_num_changes(self):
        return self.num_changes

    def set_num_changes(self, num_changes):
        self.num_changes = num_changes

    def get_code_churn(self):
        return self.code_churn

    def set_code_churn(self,code_churn):
        self.code_churn = code_churn

    def get_num_authors(self):
        return self.num_authors

    def set_num_authors(self,num_authors):
        self.num_authors = num_authors

    def get_unique_authors(self):
        return self.unique_authors

    def set_unique_authors(self,unique_authors):
        self.unique_authors = unique_authors

class analysis(object):

    def __init__(self, log_file_path, issue_file_path):
        self.log_file_path = log_file_path
        self.issue_file_path = issue_file_path
        self.file_dict =  {}
        self.author = ""
        self.issueId = ""
        self.ignore_line = False
        self.commit_issue = set()

    def read_issue_file(self):
        f = open(self.issue_file_path,'r')
        issue_set = set()

        for issue_id in f:
            issue_set.add(issue_id.rstrip('\n'))

        return issue_set

    def read_log_file(self,iss_set):
        f = open(self.log_file_path,'r',encoding='utf-8')

        commit_num = 0

        for line in f:
            line = line.strip('\n')
            if line.startswith("commit"):
                commit_num = commit_num + 1
                self.author = ""
                self.issueId = ""

            if len(line) <= 2:
                continue

            if line.startswith("Author"):
                self.author = line.split(":")[1].split("<")[0].lstrip().rstrip()

            if (line.__contains__("YARN") or
                    line.__contains__("MAPREDUCE") or
                    line.__contains__("HDFS") or
                    line.__contains__("HADOOP")):
                # self.issueId = line.split(".")[0].lstrip().rstrip()
                line = line.lstrip().rstrip()
                self.issueId = line.split(' ')[0].strip('.')

            if (line.endswith("+") or line.endswith("-")) and line.__contains__("java"):
                filepath = line.split("|")[0].lstrip().rstrip()
                changes = line.split("|")[1].lstrip().rstrip().split(' ')[0]

                if self.issueId == 'HDFS-9788':
                    print(self.issueId in iss_set)

                if self.issueId in iss_set and self.author != "" and self.issueId != "" and filepath.__contains__(".java"):
                    self.commit_issue.add(self.issueId)
                    if self.file_dict.__contains__(filepath):
                        info = self.file_dict[filepath]

                        # updata the file info
                        info.set_num_changes(info.get_num_changes() + 1)
                        info.set_code_churn(info.get_code_churn() + int(changes))

                        author_set = info.get_unique_authors()

                        if self.author not in author_set:
                            author_set.add(self.author)
                            info.set_num_authors(info.get_num_authors() + 1)
                        else:
                            # do nothing
                            pass
                    else:
                        info = fileInfo()

                        # updata the file info
                        info.set_num_changes(1)
                        info.set_code_churn(int(changes))

                        author_set = info.get_unique_authors()

                        if self.author not in author_set:
                            author_set.add(self.author)
                            info.set_num_authors(1)

                        self.file_dict[filepath] = info
        print(commit_num)

    def main(self):
        iss_set = self.read_issue_file()
        self.read_log_file(iss_set)
        print(self.file_dict)
        writeCSV.read_writeCSV("a","a",self.file_dict)
        for iss in self.commit_issue:
            print(iss)


if __name__ == '__main__':
    analysis('./postreleaseLog.txt','./postReleaseIssue.txt').main()
