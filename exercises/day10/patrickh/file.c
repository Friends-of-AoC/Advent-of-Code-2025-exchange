#include <gsl/gsl_linalg.h>
#include <stdio.h>
#include <string.h>
#include <limits.h>

static void print_v(double *v, int e_count) {
	for (int i = 0; i < e_count; ++i)
		printf(i ? " %g" : "%g", v[i]);
}

static void print_m(double *m, int r_count, int c_count) {
	for (int r = 0; r < r_count; ++r) {
		fputs("  ", stdout);
		print_v(m + r * c_count, c_count);
		fputc('\n', stdout);
	}
}

static int is_known(gsl_matrix_view m, size_t vari, size_t depth) {
	size_t lines = m.matrix.size1;
	size_t cols = m.matrix.size2;
	if (depth > lines)
		return 0;
	for (size_t l = 0; l < lines; ++l) {
		if (m.matrix.data[l * cols + vari] != 0) {
			for (size_t c = 0; c < cols; ++c) {
				if (c != vari && m.matrix.data[l * cols + c] != 0
						&& !is_known(m, c, depth + 1))
					goto unknown_line;
			}
			return 1;
		}
		unknown_line: ;
	}
	return 0;
}

static long min_presses(gsl_matrix_view batteryXbutton, gsl_vector_view joltage) {
	size_t lines = batteryXbutton.matrix.size1;
	size_t cols = batteryXbutton.matrix.size2;
	if (joltage.vector.size != lines)
		abort();

//	puts("min-presses:\n IN-M:");
//	print_m(batteryXbutton.matrix.data, lines, cols);
//	fputs(" IN-V:\n  ", stdout);
//	print_v(joltage.vector.data, lines);

	gsl_permutation *perm = gsl_permutation_alloc(lines);

	// Decompose A into the LU form:
	int signum;
	int res = gsl_linalg_LU_decomp(&batteryXbutton.matrix, perm, &signum);
	if (res == GSL_SUCCESS) {
		gsl_vector *x = gsl_vector_alloc(lines);
		// Solve the linear system
		gsl_linalg_LU_solve(&batteryXbutton.matrix, perm, &joltage.vector, x);
		long reuslt = 0;
		for (size_t i = 0; i < lines; ++i) {
			if (x->data[i] < 0 || x->data[i] != (double) (int) x->data[i])
				return -1;
			reuslt += x->data[i];
		}
		gsl_permutation_free(perm);
		gsl_vector_free(x);
		return reuslt;
	}
//	puts("\n M:");
//	print_m(batteryXbutton.matrix.data, lines, cols);
//	fputs(" V:\n  ", stdout);
//	print_v(joltage.vector.data, lines);
//	fputs("\n perm:\n ", stdout);
//	gsl_permutation_fprintf(stdout, perm, " %.1g");
//	fputc('\n', stdout);

	long maxpress = 0;
	for (size_t i = 0; i < lines; ++i) {
		if (joltage.vector.data[i] < 0
				|| joltage.vector.data[i]
						!= (double) (int) joltage.vector.data[i])
			(printf("err: %g\n", joltage.vector.data[i]), abort());
		maxpress += joltage.vector.data[i];
	}
	gsl_matrix *new = gsl_matrix_alloc(lines + 1, cols);
	memcpy(new->data, batteryXbutton.matrix.data,
			sizeof(double) * lines * cols);
	memset(new->data + lines * cols, 0, sizeof(double) * cols);
	gsl_vector *vec = gsl_vector_alloc(lines + 1);
	memcpy(vec->data, joltage.vector.data, sizeof(double) * lines);
	long result = -1;
	size_t vari = cols - 1;
	while (is_known(batteryXbutton, vari, 0))
		--vari;
	new->data[lines * cols + vari] = 1;
	for (long press = 0; press < maxpress; ++press) {
		vec->data[lines] = press;
		gsl_matrix_view mview = { *new };
		gsl_vector_view vview = { *vec };
		long mp = min_presses(mview, vview);
		if (mp != -1 && (result == -1 || mp < result))
			result = mp;
	}
	return result;
}

int main(void) {
	puts("hello");
	double m_data[] = {/*
	 1, 1, 0, 0, //
	 0, 1, 0, 0, //
	 0, 0, 1, 0, //
	 0, 0, 0, 1, //*/
	/*	  */0, 0, 0, 0, 1, 1, //
			0, 1, 0, 0, 0, 1, //
			0, 0, 1, 1, 1, 0, //
			1, 1, 0, 1, 0, 0, //
//
			0, 0, 0, 0, 0, 0, //
			0, 0, 0, 0, 0, 0, //
			};
	double v_data[] = { //
			/*	  */3, //
					5, //
					4, //
					7, //
//
					0, //
					0, //
			};
	gsl_matrix_view m = gsl_matrix_view_array(m_data, 4, 6);
	gsl_vector_view v = gsl_vector_view_array(v_data, 4);

	// Print the values of A and b using GSL print functions
	puts("IN-M:");
	print_m(m_data, 4, 6);
	fputs("IN-V:\n  ", stdout);
	print_v(v_data, 4);
	fputc('\n', stdout);

//	// Allocate memory for the solution vector x and the permutation perm:
//	gsl_vector *x = gsl_vector_alloc(4);
//	gsl_permutation *perm = gsl_permutation_alloc(4);
//
//	// Decompose A into the LU form:
//	int signum;
//	int res = gsl_linalg_LU_decomp(&m.matrix, perm, &signum);
//
//	printf("\nres=%d\nsig=%d\n", res, signum);
//
//	puts("M:");
//	print_m(m_data, 4, 6);
//	fputs("perm:\n ", stdout);
//	gsl_permutation_fprintf(stdout, perm, " %.1g");
//
//	if (res != GSL_SUCCESS) {
//		printf("\nU(%1$d,%1$d)=%2$g\n", res, m_data[6 * res + res - 7]);
//
//		gsl_matrix_view m = gsl_matrix_view_array(m_data, 5, 6);
//	} else {
//		puts("\nsuccess");
//		// Solve the linear system
//		gsl_linalg_LU_solve(&m.matrix, perm, &v.vector, x);
//
//		fputs("OUT-V:\n  ", stdout);
//		print_v(x->data, 4);
//	}
	long reuslt = min_presses(m, v);
	printf("result=%ld\n", reuslt);
	return 0;
}
