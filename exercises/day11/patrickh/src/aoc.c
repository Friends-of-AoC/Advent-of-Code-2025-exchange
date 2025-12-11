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

#include <gsl/gsl_linalg.h>
#include <gsl/gsl_multiroots.h>

#ifdef INTERACTIVE
#define INTERACT(...) __VA_ARGS__
#else
#define INTERACT(...)
#endif

struct data* read_data(const char *path);

int year = 2025;
int day = 11;
int part = 2;
FILE *solution_out;
int is_test_data = 0;
#ifdef INTERACTIVE
int interactive = 0;
#else
#define interactive 0
#endif

#define starts_with(str, start) !memcmp(str, start, sizeof(start) - 1)

#define IDX_MAX SIZE_MAX
typedef size_t idx;
typedef ssize_t pos;

#define NUM_MAX UINT16_MAX
typedef uint16_t num;

struct device {
	char *name;
	char **out_names;
	struct device **out_devs;
	size_t out_size;
};

struct data {
	size_t devices_count;
	size_t devices_alloc;
	struct device *devices;
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

static void print(FILE *str, struct data *data, uint64_t result) {
	if (!do_print && !interactive)
		return;
	fprintf(str, "%sresult=%"I64"u\n%s", STEP_HEADER, result, STEP_BODY);
	fputs(interactive ? STEP_FINISHED : RESET, str);
}

static uint64_t count(struct device *src) {
	uint64_t result = 0;
	for (idx i = 0; i < src->out_size; ++i)
		if (src->out_devs[i])
			result += count(src->out_devs[i]);
		else if (!strcmp("out", src->out_names[i]))
			++result;
	return result;
}

const char* solve(const char *path) {
	struct data *data = read_data(path);
	uint64_t result = 0;
	struct device *you = NULL;
	for (idx i = 0; i < data->devices_count; ++i) {
		struct device *d = data->devices + i;
		d->out_devs = calloc(d->out_size, sizeof(struct device*));
		if (!strcmp("you", d->name))
			you = d;
		for (idx oi = 0; oi < d->out_size; ++oi) {
			char *name = d->out_names[oi];
			for (idx di = 0; di < data->devices_count; ++di) {
				if (!strcmp(name, data->devices[di].name)) {
					d->out_devs[oi] = data->devices + di;
					break;
				}
			}
			if (!d->out_devs[oi] && strcmp("out", name))
				(printf("the device %s does not exists\n", name), abort());
		}
	}
	if (!you)
		abort();
	print(solution_out, data, result);
	result = count(you);
	print(solution_out, data, result);
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
	if (data->devices_alloc == data->devices_count) {
		data->devices_alloc += 64;
		data->devices = reallocarray(data->devices, data->devices_alloc,
				sizeof(struct device));
	}
	line = strdup(line);
	char *end;
	for (end = line; islower(*end); ++end)
		;
	if (*end != ':' || end == line)
		abort();
	struct device *d = data->devices + data->devices_count++;
	*end = '\0';
	d->name = line;
	size_t dst_alloc = 0;
	size_t dst_size = 0;
	char **dst = NULL;
	for (line = end + 1; *line && isspace(*line); ++line)
		;
	do {
		if (dst_alloc == dst_size) {
			dst_alloc += 64;
			dst = reallocarray(dst, dst_alloc, sizeof(char*));
		}
		if (!islower(*line))
			abort();
		for (end = line + 1; islower(*end); ++end)
			;
		if (!*end && !isspace(*end))
			abort();
		*end = '\0';
		dst[dst_size++] = line;
		for (line = end + 1; *line && isspace(*line); ++line)
			;
	} while (*line);
	dst = reallocarray(dst, dst_size, sizeof(char*));
	d->out_size = dst_size;
	d->out_names = dst;
	for (line = end + 1; *line && isspace(*line); ++line)
		;
	if (*line)
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
			INTERACT(force_non_interactive = 1);
		} else if (!strcmp("print", argv[idx])) {
			idx++;
			do_print = 1;
			INTERACT(force_non_interactive = 1);
		} else if (!strcmp("non-interactive", argv[idx])) {
			idx++;
			INTERACT(force_non_interactive = 1);
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
