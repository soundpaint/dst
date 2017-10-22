include common.mk
ROOT=.

all:
	cd java ; make all
	cd media ; make all

run:
	cd java ; make run

objclean:
	rm -rf $(BUILD)

bkpclean:
	cd java ; make bkpclean
	cd media ; make bkpclean
	rm -f *~

coreclean:
	cd java ; make coreclean
	cd media ; make coreclean
	rm -f core core.*

clean: objclean

distclean: objclean bkpclean

tarball: distclean
	@TGZ_DATE=`date +%Y-%m-%d_%H-%M-%S` ; \
	PROJECT_NAME=dst ; \
	PROJECT_PATH=`basename \`pwd\`` ; \
	TGZ_PREFIX=$$PROJECT_NAME\_$$TGZ_DATE ; cd .. ; \
	tar cvf ./$$TGZ_PREFIX.tar.bz2 \
		--exclude='untracked-files' \
		--exclude='untracked-files/*' \
		--transform=s/$$PROJECT_PATH/$$TGZ_PREFIX/ \
		--bzip2 $$PROJECT_PATH

#  Local Variables:
#    coding:utf-8
#    mode:Makefile
#  End:
