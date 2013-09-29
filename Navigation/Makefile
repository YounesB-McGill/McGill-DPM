# MacOSX gcc 4.2.1

FILES := Lab3 Navigator
BDIR  := bin
SDIR  := src
OBJS  := $(patsubst %,$(BDIR)/%.class,$(FILES))
SRCS  := $(patsubst %,$(SDIR)/%.java,$(FILES))

CC   := nxjc
CF   := -g:none -O -verbose -d $(BDIR) $(SDIR)/*.java

default: $(OBJS)

$(BDIR)/%.class: $(SDIR)/%.java
	@mkdir -p $(BDIR)
	$(CC) $(CF) $?

.PHONY: clean

clean:
	-rm $(OBJS)
