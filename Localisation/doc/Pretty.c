/* DPM lab 4 */

#include <stdlib.h> /* malloc free */
#include <stdio.h>  /* fprintf */
#include <math.h>

/* hack */
#define N 10

/** private (entry point) */
int main(int argc, char **argv) {
	double hack;
	double start[N], final[N], reported[N], d[N];
	double sum, ssq;
	double mean, var, stdd;
	int i;
	char buffer[120], buffer2[120], *s, *t;
	char *label = "none";

	if(2 <= argc) label = argv[1];
	for(i = 0; i < N; i++) {
		scanf("%lf\t%lf\t%lf\n", &start[i], &reported[i], &final[i]);
	}

	printf("Calculate the differences in Equation~\\ref{%s-d1}--\\ref{%s-d%i}.\n\n", label, label, N);
	for(i = 0, sum = 0, ssq = 0; i < N; i++) {
		hack = final[i];
		if(hack >= 180.0)      hack -= 360.0;
		d[i] = hack - reported[i];
		if(d[i] >= 180.0)      d[i] -= 360.0;
		else if(d[i] < -180.0) d[i] += 360.0;
		/* compute std dev */
		sum += d[i];
		ssq += d[i] * d[i];

		/* bah */
		printf("\\begin{align}\n");
		printf("d_{%i} &= ((%.0f) - (%.4f))_{360[-180,180]} \\nonumber\\\\\n", i+1, final[i], reported[i]);
		printf(" &= %.4f \\label{%s-d%i}\n", d[i], label, i+1);
		printf("\\end{align}\n");
	}
	printf("\n");

	printf("Calculate the sum of the differences (Equation~\\ref{%s-d1}--\\ref{%s-d%i}) in Equation~\\ref{%s-sum}.\n\n", label, label, N, label);
	printf("\\begin{align}\n");
	printf("\\text{sum} &= \\sum_{i=1}^{%i} d_{i} \\nonumber\\\\\n", N);
	printf(" &= ", N);
	for(i = 0; i < N; i++) {
		printf("(%.4f)", d[i]);
		if(i != N - 1) printf(" + \\nonumber\\\\\n &\\quad\\quad ");
	}
	printf(" \\nonumber\\\\\n");
	printf(" &= %.4f \\label{%s-sum}\n", sum, label);
	printf("\\end{align}\n\n");

	printf("Calculate the sum of the differences (Equation~\\ref{%s-d1}--\\ref{%s-d%i}) squared in Equation~\\ref{%s-sum2}.\n\n", label, label, N, label);
	printf("\\begin{align}\n");
	printf("\\text{ssq} &= \\sum_{i=1}^{%i} d_{i}^{\\phantom{i}2} \\nonumber\\\\\n", N);
	printf(" &= ", N);
	for(i = 0; i < N; i++) {
		printf("(%.2f)^2", d[i]);
		if(i != N - 1) printf(" + \\nonumber\\\\\n &\\quad\\quad ");
	}
	printf(" \\nonumber\\\\\n");
	printf(" &= %.2f \\label{%s-sum2}\n", ssq, label);
	printf("\\end{align}\n\n");
	
	mean = sum / N;
	var  = (ssq - sum*sum/N) / (N-1);
	stdd = sqrt(var);
	
	printf("Calculate the mean from Equation~\\ref{%s-sum} in Equation~\\ref{%s-mean}.\n\n", label, label);
	printf("\\begin{align}\n");
	printf("\\text{mean} &= \\frac{\\text{sum}}{N} \\nonumber\\\\\n");
	printf(" &= \\frac{%.4f}{%i} \\nonumber\\\\\n", sum, N);	
	printf(" &= %f \\label{%s-mean}\n", mean, label);
	printf("\\end{align}\n\n");

	printf("Calculate the variance from Equation~\\ref{%s-sum} and \\ref{%s-sum2} in Equation~\\ref{%s-var}.\n\n", label, label, label);
	printf("\\begin{align}\n");
	printf("\\sigma^{2} &= \\frac{\\text{ssq} - \\frac{\\text{sum}^{2}}{N}}{N-1} \\nonumber\\\\\n");
	printf(" &= \\frac{%.4f - \\frac{%.4f^2}{%i}}{%i-1} \\nonumber\\\\\n", ssq, sum, N, N);
	printf(" &= %f \\label{%s-var}\n", var, label);
	printf("\\end{align}\n\n");

	printf("Calculate the corrected sample standard deviation from the variance (Equation~\\ref{%s-var}) in Equation~\\ref{%s-stdd}.\n\n", label, label);
	printf("\\begin{align}\n");
	printf("\\sigma &= \\sqrt{\\sigma^{2}} \\nonumber\\\\\n");
	printf(" &= \\sqrt{%f} \\nonumber\\\\\n", var);
	printf(" &= %f \\label{%s-stdd}\n", stdd, label);
	printf("\\end{align}\n\n");

	/*stderr = sqrt((ssq/N - mean*mean) / (double)N);*/

	printf("\\begin{table*}[htb]\n");
	printf("\\begin{center}\\begin{tabular}{r@{}l r@{}l r@{}l r@{}l}\n");
	/* printf("&initial&&& &final&&& &real&& \\\\\n"); */
	printf("&$\\theta_{\\text{start}}$ (\\degree)& &$\\theta_{\\text{final}}$ (\\degree)& &$\\theta_{\\text{reported}}$ (\\degree)& &$\\theta_{\\text{error}}$ (cm) \\\\\n");
	printf("\\hline\n");
	for(i = 0; i < N; i++) {
		snprintf(buffer, sizeof(buffer), "%.0f&& %.0f&& %.4f& %.4f \\\\", start[i], final[i], reported[i], d[i]);
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
	printf("\\caption{$\\theta_{\\text{start}}$ is the starting orientation of the robot.\n");
	printf("The error mean is $%.4f$, variance is $%.4f$, and the corrected sample standard deviation is $%.4f$.}\n", mean, var, stdd);
	printf("\\label{%s}\n", label);
	printf("\\end{table*}\n");

	//printf("mean %f, varience %f, corrected sample stadard deviation %f\n", mean, var, stdd);

	return EXIT_SUCCESS;
}
