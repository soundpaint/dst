include ../common.mk
ROOT=..
BUILD_JAVA=$(BUILD)/java
SRC_DST = $(wildcard org/soundpaint/dst/*.java)
SRC_DSTX = $(wildcard org/soundpaint/dstx/*.java)
SRC = $(SRC_DST) $(SRC_DSTX)
OBJ = $(patsubst %,$(BUILD_JAVA)/%,$(SRC:.java=.class))

all: $(BUILD_JAVA) $(OBJ)

run: all
	java -cp $(BUILD_JAVA) org.soundpaint.dst.ExampleApplication

$(BUILD_JAVA):
	mkdir -p $@

$(BUILD_JAVA)/%.class: %.java
	javac -d $(BUILD_JAVA) -Xlint:all -Xdiags:verbose $<

bkpclean:
	rm -f *~

coreclean:
	rm -f core core.*
