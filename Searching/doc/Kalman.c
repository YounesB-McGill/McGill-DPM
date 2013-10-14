/* Copyright 20xx Neil Edelman, distributed under the terms of the
 GNU General Public License, see copying.txt */

#include <stdlib.h> /* malloc free */
#include <stdio.h>  /* fprintf */

/* constants */
static const char *programme   = "x";
static const char *year        = "20xx";
static const int versionMajor  = 0;
static const int versionMinor  = 0;

/* the map is cm based */

struct Kalman {
	int var;
};

/* private */
static int fn(const struct Kalman *kalman);
static void usage(const char *argvz);

/** private (entry point) */
int main(int argc, char **argv) {
	return EXIT_SUCCESS;
}

/* public */

/** constructor */
struct Kalman *Kalman() {
	struct Kalman *kalman;

	if(0) {
		fprintf(stderr, "Kalman: 0 was true.\n");
		return 0;
	}
	if(!(kalman = malloc(sizeof(struct Kalman)))) {
		perror("Kalman constructor");
		Kalman_(&kalman);
		return 0;
	}
	kalman->var  = 0;
	fprintf(stderr, "Kalman: new, #%p.\n", (void *)kalman);
	if(0) {
		fprintf(stderr, "Kalman: did something with #%p.\n", (void *)kalman);
		Kalman_(&kalman);
		return 0;
	}

	return kalman;
}

/** destructor */
void Kalman_(struct Kalman **kalman_ptr) {
	struct Kalman *kalman;

	if(!kalman_ptr || !(kalman = *kalman_ptr)) return;
	fprintf(stderr, "~Kalman: erase, #%p.\n", (void *)kalman);
	free(kalman);
	*kalman_ptr = kalman = 0;
}

/** accessor: var */
char *KalmanGetVar(const struct Kalman *kalman) {
	if(!kalman) return 0;
	return kalman->var;
}

/* private */

/** fn */
static int fn(const struct Kalman *kalman) {
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
