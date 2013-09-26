/* Copyright 20xx Neil Edelman, distributed under the terms of the
 GNU General Public License, see copying.txt */

#include <stdlib.h> /* malloc free */
#include <stdio.h>  /* fprintf */
#include "Oo.h"

/* constants */
static const char *programme   = "x";
static const char *year        = "20xx";
static const int versionMajor  = 0;
static const int versionMinor  = 0;

struct Oo {
	int var;
};

/* private */
static int fn(const struct Oo *oo);
static void usage(const char *argvz);

/** private (entry point) */
int main(int argc, char **argv) {
	return EXIT_SUCCESS;
}

/* public */

/** constructor */
struct Oo *Oo() {
	struct Oo *oo;

	if(0) {
		fprintf(stderr, "Oo: 0 was true.\n");
		return 0;
	}
	if(!(oo = malloc(sizeof(struct Oo)))) {
		perror("Oo constructor");
		Oo_(&oo);
		return 0;
	}
	oo->var  = 0;
	fprintf(stderr, "Oo: new, #%p.\n", (void *)oo);
	if(0) {
		fprintf(stderr, "Oo: did something with #%p.\n", (void *)oo);
		Oo_(&oo);
		return 0;
	}

	return oo;
}

/** destructor */
void Oo_(struct Oo **oo_ptr) {
	struct Oo *oo;

	if(!oo_ptr || !(oo = *oo_ptr)) return;
	fprintf(stderr, "~Oo: erase, #%p.\n", (void *)oo);
	free(oo);
	*oo_ptr = oo = 0;
}

/** accessor: var */
char *OoGetVar(const struct Oo *oo) {
	if(!oo) return 0;
	return oo->var;
}

/* private */

/** fn */
static int fn(const struct Oo *oo) {
	return 0;
}

static void usage(const char *argvz) {
	fprintf(stderr, "Usage: %s\n", argvz);
	fprintf(stderr, "Version %d.%d.\n\n", versionMajor, versionMinor);
	fprintf(stderr, "%s Copyright %s Neil Edelman\n", programme, year);
	fprintf(stderr, "This program comes with ABSOLUTELY NO WARRANTY.\n");
	fprintf(stderr, "This is free software, and you are welcome to redistribute it\n");
	fprintf(stderr, "under certain conditions; see copying.txt.\n\n");
}
