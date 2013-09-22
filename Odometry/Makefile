# MacOSX gcc 4.2.1

FILES := Lab2 Odometer OdometryCorrection OdometryDisplay SquareDriver
BDIR  := bin
SDIR  := src
OBJS  := $(patsubst %,$(BDIR)/%.class,$(FILES))
SRCS  := $(patsubst %,$(SDIR)/%.java,$(FILES))

CC   := nxjc
CF   := -g:none -O -verbose -d $(BDIR) $(SDIR)/*.java

default: $(OBJS)

#$(BDIR)/$(): $(OBJS)
#	$(CC) $(CF) $^ -o $@

$(BDIR)/%.class: $(SDIR)/%.java
	@mkdir -p $(BDIR)
	$(CC) $(CF) $?
#	$(CC) $(CF) -c $? -o $@

.PHONY: clean

clean:
	-rm $(OBJS)
