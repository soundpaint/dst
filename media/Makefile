include ../common.mk
ROOT=..
BUILD_JAVA=$(realpath $(BUILD)/java)
BUILD_MEDIA=$(BUILD)/media
SRC_PDF=$(wildcard *.pdf)
OBJ_PDF=$(patsubst %,$(BUILD_MEDIA)/%,$(SRC_PDF:.pdf=.pdf))
OBJ_DFT_PPM=$(BUILD_MEDIA)/spectrum_dft.ppm
OBJ_PPM_MU_LIST=0.90000 0.99000 0.99900 0.99927 0.99990 0.99999
OBJ_DST_PPM=$(foreach MU,$(OBJ_PPM_MU_LIST),$(BUILD_MEDIA)/spectrum_dst_$(MU).ppm)
OBJ_PPM=$(OBJ_DFT_PPM) $(OBJ_DST_PPM)
OBJ_PNG=$(subst .ppm,.png,$(OBJ_PPM))
OBJ=$(OBJ_PDF) $(OBJ_PNG)

all: $(BUILD_MEDIA) $(OBJ)

$(BUILD_MEDIA):
	mkdir -p $@

$(BUILD_MEDIA)/%.pdf: %.pdf
	cp -p $< $@

$(BUILD_MEDIA)/spectrum_df%.ppm:
	java -cp $(BUILD_JAVA) org.soundpaint.dst.ExampleApplication \
	--dft --out $@

$(BUILD_MEDIA)/spectrum_dst_%.ppm:
	java -cp $(BUILD_JAVA) org.soundpaint.dst.ExampleApplication \
	--mu0 $* --dst --out $@

%.png: %.ppm
	pnmtopng -verbose $< > $@

bkpclean:
	rm -f *~
