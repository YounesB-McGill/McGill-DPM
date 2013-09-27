/* DPM lab 2 */

#include <stdlib.h> /* malloc free */
#include <stdio.h>  /* fprintf */
#include <math.h>

#define N 10

struct vec2_t {
	double x, y;
};

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

	printf("\\begin{table}[htb]\n");
	printf("\\begin{center}\\begin{tabular}{r r@{}l r@{}l r@{}l}\n");
	printf("&actual&&& &reported&&& &delta&&& \\\\\n");
	printf("&x (cm)& &y (cm)& &x (cm)& &y (cm)& &x (cm)& &y (cm)& \\\\\n");
	printf("\\hline\n");
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
		printf("%s\n", buffer2);
	}
	printf("\\end{tabular}\\end{center}\n");
	printf("\\caption{Reported error as read by the robot, and real error as read by a ruler and the difference between them for the [un]corrected code.\n");
	printf("\\caption{The corrected sample standard deviation of the difference of $x$ is %.2f and $y$ is %.2f.}\n", stdd, 0.0);
	printf("\\label{[a|b]}\n");
	printf("\\end{table}\n");

	printf("mean %f, varience %f, corrected sample stadard deviation %f\n", mean, var, stdd);

	return EXIT_SUCCESS;
}
