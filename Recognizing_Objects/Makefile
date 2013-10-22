# MacOSX gcc 4.2.1

FILES := Lab5 LCDInfo Colour LightLocalizer Navigation Odometer TwoWheeledRobot USLocalizer UltrasonicListener Detection Controller
BDIR  := bin
SDIR  := src
OBJS  := $(patsubst %,$(BDIR)/%.class,$(FILES))
SRCS  := $(patsubst %,$(SDIR)/%.java,$(FILES))
BACK  := backup

CC   := nxjc
CF   := -g:none -O -verbose -d $(BDIR) $(SDIR)/*.java

default: $(OBJS)

$(BDIR)/%.class: $(SDIR)/%.java
	@mkdir -p $(BDIR)
	$(CC) $(CF) $?

.PHONY: clean backup

backup:
	@mkdir -p $(BACK)
	zip $(BACK)/$(INST)-`date +%Y-%m-%dT%H%M%S`.zip Makefile $(SRCS)

clean:
	-rm $(OBJS)
