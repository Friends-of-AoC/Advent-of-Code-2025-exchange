/*
 * aoc.c
 *
 *  Created on: Dec 2, 2024
 *      Author: pat
 */

#include "aoc.h"

#include "color.h"
#include "hash.h"
#include "interactive.h"

#include <bits/stdint-intn.h>
#include <bits/stdint-uintn.h>
#include <bits/types/clock_t.h>
#include <bits/types/FILE.h>
#include <ctype.h>
#include <stdarg.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <math.h>
#include <time.h>

#ifdef INTERACTIVE
#define INTERACT(...) __VA_ARGS__
#else
#define INTERACT(...)
#endif

struct data* read_data(const char *path);

int year = 2025;
int day = 9;
int part = 2;
FILE *solution_out;
int is_test_data = 0;
#ifdef INTERACTIVE
int interactive = 0;
#else
#define interactive 0
#endif

#define starts_with(str, start) !memcmp(str, start, sizeof(start) - 1)

typedef size_t idx;
typedef ssize_t pos;

#define NUM_MAX UINT64_MAX
typedef uint64_t num;

#define POS1D_MIN SSIZE_MIN
#define POS1D_MAX SSIZE_MAX
typedef pos pos1d;
struct pos2d {
	pos1d x, y;
};

struct connection {
	idx a, b;
	long double distance;
};

struct data {
	size_t pos_count;
	size_t pos_alloc;
	struct pos2d *positions;
};

static int do_print = 1;

#if 0
static void print_step(FILE *str, uint64_t result, char *format, ...) __attribute__ ((__format__ (__printf__, 3, 4)));

static void print_step(FILE *str, uint64_t result, char *format, ...) {
	if (result) {
		fprintf(str, "%sresult=%"I64"u\n%s", STEP_HEADER, result, STEP_BODY);
	} else {
		fputs(STEP_BODY, str);
	}
	if (!do_print && !interactive) {
		return;
	}
	va_list list;
	va_start(list, format);
	vfprintf(str, format, list);
	if (interactive)
		fputs(STEP_FINISHED, str);
}
#endif

#if 0
static void print_space(FILE *str, uint64_t count) {
	uint64_t val;
	for (val = 0; val + INT_MAX < count; val += INT_MAX)
		fprintf(str, "%*s", INT_MAX, "");
	fprintf(str, "%*s", (int) (count - val), "");
}
#endif

static void print(FILE *str, struct data *data, uint64_t result,
		struct pos2d *min, struct pos2d *max) {
	if (!do_print && !interactive)
		return;
	if (result || 1)
		fprintf(str, "%sresult=%"I64"u\n%s", STEP_HEADER, result, STEP_BODY);
	fputs(interactive ? STEP_FINISHED : RESET, str);
}

static int pos2dcmp(const void *a, const void *b) {
	const struct pos2d *pa = a, *pb = b;
	if (pa->y < pb->y)
		return -1;
	if (pa->y > pb->y)
		return 1;
	if (pa->x < pb->x)
		return -1;
	if (pa->x > pb->x)
		return 1;
	return 0;
}

const char* solve(const char *path) {
	struct data *data = read_data(path);
	uint64_t result = 0;
	qsort(data->positions, data->pos_count, sizeof(struct pos2d), pos2dcmp);
	print(solution_out, data, result, NULL, NULL);
	struct pos2d world_start = data->positions[0];
	struct pos2d world_end = data->positions[data->pos_count - 1];
	for (idx i = 0; i < data->pos_count; ++i) {
		struct pos2d *p = data->positions + i;
		if (p->x < world_start.x)
			world_start.x = p->x;
		if (p->x > world_end.x)
			world_start.x = p->x;
	}
	for (idx mini = 0; mini + 1 < data->pos_count; ++mini) {
		struct pos2d minp = data->positions[mini];
		for (idx maxi = mini + 1; maxi < data->pos_count; ++maxi) {
			struct pos2d maxp = data->positions[maxi];
			size_t area = maxp.y - minp.y + 1;
			if (maxp.x > minp.x)
				area *= maxp.x - minp.x + 1;
			else
				area *= minp.x - maxp.x + 1;
			if (area <= result)
				continue;
			for (idx midi = mini + 1; midi < maxi; ++midi) {
				struct pos2d midp = data->positions[maxi];
				if (midp.y == maxp.y)
					break;
				if (midp.y == minp.y)
					continue;
				if ((midp.x > minp.x && midp.x < maxp.x)
						|| (midp.x < minp.x && midp.x > maxp.x))
					goto inval;
			}
			result = area;
			print(solution_out, data, result, &minp, &maxp);
			inval: ;
		}
	}
	print(solution_out, data, result, NULL, NULL);
	free(data);
	return u64toa(result);
}

static struct data* parse_line(struct data *data, char *line) {
	for (; *line && isspace(*line); ++line)
		;
	if (!*line)
		return data;
	if (!data) {
		data = calloc(1, sizeof(struct data));
	}
	if (data->pos_alloc == data->pos_count) {
		data->pos_alloc += 64;
		data->positions = reallocarray(data->positions, data->pos_alloc,
				sizeof(struct pos2d));
	}
	char *end;
	long long val = strtoll(line, &end, 10);
	if (val <= 0 || val > POS1D_MAX || errno)
		abort();
	data->positions[data->pos_count].x = val;
	if (*end != ',')
		abort();
	val = strtoll(end + 1, &end, 10);
	if (val <= 0 || val > POS1D_MAX || errno)
		abort();
	data->positions[data->pos_count].y = val;
	data->pos_count++;
	for (; *end && isspace(*end); ++end)
		;
	if (*end)
		abort();
	return data;
}

// common stuff

#if !(AOC_COMPAT & AC_POSIX)
ssize_t getline(char **line_buf, size_t *line_len, FILE *file) {
	ssize_t result = 0;
	while (21) {
		if (*line_len == result) {
			size_t len = result ? result * 2 : 64;
			void *ptr = realloc(*line_buf, len);
			if (!ptr) {
				fseek(file, -result, SEEK_CUR);
				return -1;
			}
			*line_len = len;
			*line_buf = ptr;
		}
		ssize_t len = fread(*line_buf + result, 1, *line_len - result, file);
		if (!len) {
			if (!result) {
				return -1;
			}
			if (result == *line_len) {
				void *ptr = realloc(*line_buf, result + 1);
				if (!ptr) {
					fseek(file, -result, SEEK_CUR);
					return -1;
				}
				*line_len = result + 1;
				*line_buf = ptr;
			}
			(*line_buf)[result] = 0;
			return result;
		}
		char *c = memchr(*line_buf + result, '\n', len);
		if (c) {
			ssize_t result2 = c - *line_buf + 1;
			if (result2 == *line_len) {
				void *ptr = realloc(*line_buf, result2 + 1);
				if (!ptr) {
					fseek(file, -*line_len - len, SEEK_CUR);
					return -1;
				}
				*line_len = result2 + 1;
				*line_buf = ptr;
			}
			fseek(file, result2 - result - len, SEEK_CUR);
			(*line_buf)[result2] = 0;
			return result2;
		}
		result += len;
	}
}
#endif // AC_POSIX
#if !(AOC_COMPAT & AC_STRCN)
char* strchrnul(char *str, int c) {
	char *end = strchr(str, c);
	return end ? end : (str + strlen(str));
}
#endif // AC_STRCN
#if !(AOC_COMPAT & AC_REARR)
void* reallocarray(void *ptr, size_t nmemb, size_t size) {
	size_t s = nmemb * size;
	if (s / size != nmemb) {
		errno = ENOMEM;
		return 0;
	}
	return realloc(ptr, s);
}
#endif // AC_REARR

char* u64toa(uint64_t value) {
	static char result[21];
	if (sprintf(result, "%"I64"u", value) <= 0) {
		return 0;
	}
	return result;
}

char* d64toa(int64_t value) {
	static char result[21];
	if (sprintf(result, "%"I64"d", value) <= 0) {
		return 0;
	}
	return result;
}

struct data* read_data(const char *path) {
	char *line_buf = 0;
	size_t line_len = 0;
	struct data *result = 0;
	FILE *file = fopen(path, "rb");
	if (!file) {
		perror("fopen");
		abort();
	}
	while (123) {
		ssize_t s = getline(&line_buf, &line_len, file);
		if (s < 0) {
			if (feof(file)) {
				free(line_buf);
				fclose(file);
				return result;
			}
			perror("getline failed");
			fflush(0);
			abort();
		}
		if (strlen(line_buf) != s) {
			fprintf(stderr, "\\0 character in line!");
			abort();
		}
		result = parse_line(result, line_buf);
	}
}

int main(int argc, char **argv) {
#ifdef INTERACTIVE
	int force_non_interactive = 0;
#endif
	solution_out = stdout;
	char *me = argv[0];
	char *f = 0;
	if (argc > 1) {
		if (argc > 4) {
			print_help: ;
			fprintf(stderr,
#ifdef INTERACTIVE
					"usage: %s [[non-]interactive|[no-]print] [p1|p2] [DATA]",
#else
					"usage: %s [non-interactive|[no-]print] [p1|p2] [DATA]",
#endif
					me);
			return 1;
		}
		int idx = 1;
		if (!strcmp("help", argv[idx])) {
			goto print_help;
		}
		if (!strcmp("no-print", argv[idx])) {
			idx++;
			do_print = 0;
			INTERACT(force_non_interactive = 1;)
		} else if (!strcmp("print", argv[idx])) {
			idx++;
			do_print = 1;
			INTERACT(force_non_interactive = 1;)
		} else if (!strcmp("non-interactive", argv[idx])) {
			idx++;
			INTERACT(force_non_interactive = 1;)
		}
#ifdef INTERACTIVE
		else if (!strcmp("interactive", argv[idx])) {
			idx++;
			interactive = 1;
		}
#endif
		if (idx < argc) {
			if (!strcmp("p1", argv[idx])) {
				part = 1;
				idx++;
			} else if (!strcmp("p2", argv[idx])) {
				part = 2;
				idx++;
			}
			if (!f && argv[idx]) {
				f = argv[idx++];
			}
			if (f && argv[idx]) {
				goto print_help;
			}
		}
	}
	if (!f) {
		f = "rsrc/data.txt";
	} else {
		is_test_data = 1;
		if (!strchr(f, '/')) {
			char *f2 = malloc(64);
			if (snprintf(f2, 64, "rsrc/test%s.txt", f) <= 0) {
				perror("snprintf");
				abort();
			}
			f = f2;
		}
	}
#ifdef INTERACTIVE
	if (interactive) {
		printf("execute now day %d part %d on file %s in interactive mode\n",
				day, part, f);
	}
	if (!force_non_interactive) {
		interact(f, interactive);
	}
#endif
	printf("execute now day %d part %d on file %s\n", day, part, f);
	clock_t start = clock();
	const char *result = solve(f);
	clock_t end = clock();
	if (result)
		printf("the result is %s\n", result);
	else
		puts("there is no result");
	uint64_t diff = end - start;
	printf("  I needed %"I64"u.%.6"I64"u seconds\n", diff / CLOCKS_PER_SEC,
			((diff % CLOCKS_PER_SEC) * UINT64_C(1000000)) / CLOCKS_PER_SEC);
	return EXIT_SUCCESS;
}
