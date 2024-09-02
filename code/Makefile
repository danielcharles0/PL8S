# UID = 1080
# GID = 1080

# OS = $(shell uname -s)

default:
# Linux
# ifeq ($(OS), Linux)
# 	groupadd -g "$(GID)" -f pl8s
# 	id -u pl8s || useradd -m -u "$(UID)" -g "$(GID)" pl8s
# 	mkdir -p ./volumes/pl8sdb/data ./volumes/pl8sdbadmin/pgadmin
# 	chown -R pl8s:pl8s ./volumes
# endif

clean:
	rm -rf ./volumes

clean_db:
	rm -rf ./volumes/pl8sdb/data

clean_logs:
	rm -rf ./volumes/pl8sweb/logs/*
