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
	struct Vec2_t reported[N], actual[N], d[N];
	struct Vec2_t sum, ssq;
	struct Vec2_t mean, var, stdd;
	int i;
	char buffer[120], buffer2[120], *s, *t;

	for(i = 0; i < N; i++) {
		scanf("%lf\t%lf\t%lf\t%lf\t\n", &reported[i].x, &reported[i].y, &actual[i].x, &actual[i].y);
	}

	printf("Calculate the differences in Equation~\\ref{dx1}--\\ref{dy%i}.\n\n", N);
	for(i = 0, sum.x = sum.y = 0, ssq.x = ssq.y = 0; i < N; i++) {
		d[i].x = actual[i].x - reported[i].x;
		d[i].y = actual[i].y - reported[i].y;
		/* compute std dev */
		sum.x += d[i].x;
		sum.y += d[i].y;
		ssq.x += d[i].x * d[i].x;
		ssq.y += d[i].y * d[i].y;

		/* bah */
		printf("\\begin{align}\n");
		printf("d_{x,%i} &= (%.1f) - (%.2f) \\nonumber\\\\\n", i+1, actual[i].x, reported[i].x);
		printf(" &= %.2f \\label{dx%i}\\\\\n", d[i].x, i+1);
		printf("d_{y,%i} &= (%.1f) - (%.2f) \\nonumber\\\\\n", i+1, actual[i].y, reported[i].y);
		printf(" &= %.2f \\label{dy%i}\n", d[i].y, i+1);
		printf("\\end{align}\n");
	}
	printf("\n");

	printf("Calculate the sum of the differences (Equation~\\ref{dx1}--\\ref{dy%i}) in Equation~\\ref{sumx}--\\ref{sumy}.\n\n", N);
	printf("\\begin{align}\n");
	printf("\\text{sum}_{x} &= \\sum_{i=1}^{%i} d_{x,i} \\nonumber\\\\\n", N);
	printf(" &= ", N);
	for(i = 0; i < N; i++) {
		printf("(%.2f)", d[i].x);
		if(i != N - 1) printf(" + \\nonumber\\\\\n &\\quad\\quad ");
	}
	printf(" \\nonumber\\\\\n");
	printf(" &= %.2f \\label{sumx}\n", sum.x);
	printf("\\end{align}\n\n");

	printf("\\begin{align}\n");
	printf("\\text{sum}_{y} &= \\sum_{i=1}^{%i} d_{y,i} \\nonumber\\\\\n", N);
	printf(" &= ", N);
	for(i = 0; i < N; i++) {
		printf("(%.2f)", d[i].y);
		if(i != N - 1) printf(" + \\nonumber\\\\\n &\\quad\\quad ");
	}
	printf(" \\nonumber\\\\\n");
	printf(" &= %.2f \\label{sumy}\n", sum.y);
	printf("\\end{align}\n\n");

	printf("Calculate the sum of the differences (Equation~\\ref{dx1}--\\ref{dy%i}) squared in Equation~\\ref{sum2x}--\\ref{sum2y}.\n\n", N);
	printf("\\begin{align}\n");
	printf("\\text{ssq}_{x} &= \\sum_{i=1}^{%i} d_{x,i}^{\\phantom{x,i}2} \\nonumber\\\\\n", N);
	printf(" &= ", N);
	for(i = 0; i < N; i++) {
		printf("(%.2f)^2", d[i].x);
		if(i != N - 1) printf(" + \\nonumber\\\\\n &\\quad\\quad ");
	}
	printf(" \\nonumber\\\\\n");
	printf(" &= %.2f \\label{sum2x}\n", ssq.x);
	printf("\\end{align}\n\n");
	
	printf("\\begin{align}\n");
	printf("\\text{ssq}_{y} &= \\sum_{i=1}^{%i} d_{y,i}^{\\phantom{y,i}2} \\nonumber\\\\\n", N);
	printf(" &= ", N);
	for(i = 0; i < N; i++) {
		printf("(%.2f)^2", d[i].y);
		if(i != N - 1) printf(" + \\nonumber\\\\\n &\\quad\\quad ");
	}
	printf(" \\nonumber\\\\\n");
	printf(" &= %.2f \\label{sum2y}\n", ssq.y);	
	printf("\\end{align}\n\n");
	
	mean.x = sum.x / N;
	mean.y = sum.y / N;
	var.x  = (ssq.x - sum.x*sum.x/N) / (N-1);
	var.y  = (ssq.y - sum.y*sum.y/N) / (N-1);
	stdd.x = sqrt(var.x);
	stdd.y = sqrt(var.y);
	
	printf("Calculate the mean from Equation~\\ref{sumx}--\\ref{sumy} in Equation~\\ref{meanx}--\\ref{meany}.\n\n");
	printf("\\begin{align}\n");
	printf("\\text{mean}_{x} &= \\frac{\\text{sum}_{x}}{N} \\nonumber\\\\\n");
	printf(" &= \\frac{%.2f}{%i} \\nonumber\\\\\n", sum.x, N);	
	printf(" &= %f \\label{meanx}\\\\\n", mean.x);

	printf("\\text{mean}_{y} &= \\frac{\\text{sum}_{y}}{N} \\nonumber\\\\\n");
	printf(" &= \\frac{%.2f}{%i} \\nonumber\\\\\n", sum.y, N);
	printf(" &= %f \\label{meany}\n", mean.y);
	printf("\\end{align}\n");
	
	printf("Calculate the variance from Equation~\\ref{sumx}--\\ref{sumy} and \\ref{sum2x}--\\ref{sum2y} in Equation~\\ref{varx}--\\ref{vary}.\n\n");
	printf("\\begin{align}\n");
	printf("\\sigma_{x}^{\\phantom{x}2} &= \\frac{\\text{ssq}_{x} - \\frac{\\text{sum}_{x}^{\\phantom{x}2}}{N}}{N-1} \\nonumber\\\\\n");
	printf(" &= \\frac{%.2f - \\frac{%.2f^2}{%i}}{%i-1} \\nonumber\\\\\n", ssq.x, sum.x, N, N);
	printf(" &= %f \\label{varx}\\\\\n", var.x);
	
	printf("\\sigma_{y}^{\\phantom{y}2} &= \\frac{\\text{ssq}_{y} - \\frac{\\text{sum}_{y}^{\\phantom{y}2}}{N}}{N-1} \\nonumber\\\\\n");
	printf(" &= \\frac{%.2f - \\frac{%.2f^2}{%i}}{%i-1} \\nonumber\\\\\n", ssq.y, sum.y, N, N);
	printf(" &= %f \\label{vary}\n", var.y);
	printf("\\end{align}\n");
	
	printf("Calculate the corrected sample standard deviation from the variance (Equation~\\ref{varx}--\\ref{vary}) in Equation~\\ref{stddx}--\\ref{stddy}.\n\n");
	printf("\\begin{align}\n");
	printf("\\sigma_x &= \\sqrt{\\sigma_{x}^{\\phantom{x}2}} \\nonumber\\\\\n");
	printf(" &= \\sqrt{%f} \\nonumber\\\\\n", var.x);
	printf(" &= %f \\label{stddx}\\\\\n", stdd.x);

	printf("\\sigma_y &= \\sqrt{\\sigma_{y}^{\\phantom{y}2}} \\nonumber\\\\\n");
	printf(" &= \\sqrt{%f} \\nonumber\\\\\n", var.y);
	printf(" &= %f \\label{stddy}\n", stdd.y);
	printf("\\end{align}\n");

	/*stderr = sqrt((ssq/N - mean*mean) / (double)N);*/

	printf("\\begin{table*}[htb]\n");
	printf("\\begin{center}\\begin{tabular}{r@{}l r@{}l r@{}l r@{}l r@{}l r@{}l}\n");
	printf("&actual&&& &reported&&& &delta&& \\\\\n");
	printf("&x (cm)& &y (cm)& &x (cm)& &y (cm)& &x (cm)& &y (cm) \\\\\n");
	printf("\\hline\n");
	for(i = 0; i < N; i++) {
		snprintf(buffer, sizeof(buffer), "%.1f& %.1f& %.2f& %.2f& %.1f& %.1f \\\\", actual[i].x, actual[i].y, reported[i].x, reported[i].y, d[i].x, d[i].y);
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
	printf("\\caption{Reported error as read by the robot, and real error as read by a ruler and the difference between them.\n");
	printf("The difference, as $(x, y)$, mean is $(%.2f, %.2f)$, variance is $(%.2f, %.2f)$, and the corrected sample standard deviation is $(%.2f, %.2f)$.}\n", mean.x, mean.y, var.x, var.y, stdd.x, stdd.y);
	printf("\\label{a}\n");
	printf("\\end{table*}\n");

	//printf("mean %f, varience %f, corrected sample stadard deviation %f\n", mean, var, stdd);

	return EXIT_SUCCESS;
}
