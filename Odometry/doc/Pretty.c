/* DPM lab 2 */

#include <stdlib.h> /* malloc free */
#include <stdio.h>  /* fprintf */
#include <math.h>

#define N 10

struct Vec2_t {
	double x, y;
};

/** private (entry point) */
int main(int argc, char **argv) {
	struct Vec2_t a[N], r[N], d[N];
	struct Vec2_t sum, ssq;
	struct Vec2_t mean, var, stdd;
	int i;
	char buffer[120], buffer2[120], *s, *t;

	for(i = 0; i < N; i++) {
		scanf("%lf\t%lf\t%lf\t%lf\t\n", &a[i].x, &a[i].y, &r[i].x, &r[i].y);
	}

	for(i = 0, sum.x = sum.y = 0, ssq.x = ssq.y = 0; i < N; i++) {
		d[i].x = a[i].x - r[i].x;
		d[i].y = a[i].y - r[i].y;
		/* compute std dev */
		sum.x += d[i].x;
		sum.y += d[i].y;
		ssq.x += d[i].x * d[i].x;
		ssq.y += d[i].y * d[i].y;
	}
	mean.x = sum.x / N;
	mean.y = sum.y / N;
	var.x  = (ssq.x - sum.x*sum.x/N) / (N-1);
	var.y  = (ssq.y - sum.y*sum.y/N) / (N-1);
	stdd.x = sqrt(var.x);
	stdd.y = sqrt(var.y);
	/*stderr = sqrt((ssq/N - mean*mean) / (double)N);*/

	printf("\\begin{table*}[htb]\n");
	printf("\\begin{center}\\begin{tabular}{r@{}l r@{}l r@{}l r@{}l r@{}l r@{}l}\n");
	printf("&actual&&& &reported&&& &delta&& \\\\\n");
	printf("&x (cm)& &y (cm)& &x (cm)& &y (cm)& &x (cm)& &y (cm) \\\\\n");
	printf("\\hline\n");
	for(i = 0; i < N; i++) {
		snprintf(buffer, sizeof(buffer), "%.1f& %.1f& %.2f& %.2f& %.1f& %.1f \\\\", a[i].x, a[i].y, r[i].x, r[i].y, d[i].x, d[i].y);
		/* replace '.' by '&.' */
		for(s = buffer, t = buffer2; s; s++) {
			if(t == buffer2 + sizeof(buffer2) - 2) {
				*t = '\0';
				break;
			}
			if(s[0] == '.') {
				t[0] = '&';
				t[1] = '.';
				t += 2;
			} else {
				t[0] = s[0];
				if(s[0] == '\0') break;
				t++;
			}
		}
		printf("%s\n", buffer2);
	}
	printf("\\end{tabular}\\end{center}\n");
	printf("\\caption{Reported error as read by the robot, and real error as read by a ruler and the difference between them for the [un]corrected code.\n");
	printf("The difference, as $(x, y)$, mean is $(%.2f, %.2f)$, varience is $(%.2f, %.2f)$, and the corrected sample standard deviation is $(%.2f, %.2f)$.}\n", mean.x, mean.y, var.x, var.y, stdd.x, stdd.y);
	printf("\\label{[a|b]}\n");
	printf("\\end{table*}\n");

	//printf("mean %f, varience %f, corrected sample stadard deviation %f\n", mean, var, stdd);

	return EXIT_SUCCESS;
}
