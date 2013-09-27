/* DPM lab 2 */

#include <stdlib.h> /* malloc free */
#include <stdio.h>  /* fprintf */
#include <math.h>

#define N 10

/** private (entry point) */
int main(int argc, char **argv) {
	double xa[N], ya[N], xr[N], yr[N], xd[N], yd[N];
	double sum, ssq;
	double mean, var, stdd;
	int i;
	char buffer[120], buffer2[120], *a, *b;

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
		snprintf(buffer, sizeof(buffer), "%.1f& %.1f& %.2f& %.2f& %.1f& %.1f \\\\", xa[i], ya[i], xr[i], yr[i], xd[i], yd[i]);
		/* replace '.' by '&.' */
		for(a = buffer, b = buffer2; a; a++) {
			if(b == buffer2 + sizeof(buffer2) - 2) {
				*b = '\0';
				break;
			}
			if(a[0] == '.') {
				b[0] = '&';
				b[1] = '.';
				b += 2;
			} else {
				b[0] = a[0];
				if(a[0] == '\0') break;
				b++;
			}
		}
		printf("%s\n", buffer);
		printf("%s\n", buffer2);
	}
	printf("mean %f, varience %f, corrected sample stadard deviation %f\n", mean, var, stdd);

	return EXIT_SUCCESS;
}
