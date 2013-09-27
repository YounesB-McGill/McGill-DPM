/* DPM lab 2 */

#include <stdlib.h> /* malloc free */
#include <stdio.h>  /* fprintf */
#include <math.h>

#define N 10

/** private (entry point) */
int main(int argc, char **argv) {
	int i;
	double xa[N], ya[N], xr[N], yr[N], xd[N], yd[N];
	double sum, ssq;
	double mean, var, stdd;

	for(i = 0; i < N; i++) {
		scanf("%lf\t%lf\t%lf\t%lf\t\n", &xa[i], &ya[i], &xr[i], &yr[i]);
	}

	for(i = 0, sum = 0, ssq = 0; i < N; i++) {
		xd[i] = xa[i] - xr[i];
		yd[i] = ya[i] - yr[i];
		/* compute std dev */
		sum += xd[i];
		ssq += xd[i] * xd[i];
	}
	mean   = sum / N;
	var    = (ssq - sum*sum/N) / (N-1);
	stdd   = sqrt(var);
	/*stderr = sqrt((ssq/N - mean*mean) / (double)N);*/

	printf("actual (x, y), reported (x, y), delta (x, y)\n", xa[i], ya[i], xr[i], yr[i]);
	for(i = 0; i < N; i++) {
		printf("%f, %f, %f, %f, %f, %f\n", xa[i], ya[i], xr[i], yr[i], xd[i], yd[i]);
	}
	printf("mean %f, varience %f, corrected sample stadard deviation %f\n", mean, var, stdd);

	return EXIT_SUCCESS;
}
