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
struct line2d {
	struct pos2d start;
	size_t len;
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
		struct line2d *lines, struct pos2d *min, struct pos2d *max) {
	if (!do_print && !interactive)
		return;
	if (result || 1)
		fprintf(str, "%sresult=%"I64"u\n", STEP_HEADER, result);
	if (min)
		fprintf(str, "min=(%"I64"d,%"I64"d)\n"
		/*		   */"max=(%"I64"d,%"I64"d)\n"
		/*		   */"len=(%"I64"d,%"I64"d)=%"I64"d\n%s", (int64_t) min->x,
				(int64_t) min->y, (int64_t) max->x, (int64_t) max->y,
				(int64_t) (max->x - min->x + 1),
				(int64_t) (max->y - min->y + 1),
				(int64_t) (max->x - min->x + 1) * (int64_t) (max->y - min->y + 1),
				STEP_BODY);
	else
		fputs(STEP_BODY, str);
	size_t line_len = 0;
	size_t line_count = 0;
	for (idx i = 0; i < data->pos_count; ++i) {
		if (data->positions[i].x >= line_len)
			line_len = data->positions[i].x + 1;
		if (data->positions[i].y >= line_count)
			line_count = data->positions[i].y + 1;
	}
	line_len++;
	line_count++;
	char *line = malloc(line_len * 2);
	char *lastline = line + line_len;
	memset(line, '.', line_len * 2);
	for (int l = 0; l < line_count; ++l) {
		for (idx i = 0; i < (data->pos_count >> 1); ++i) {
			struct line2d *ld = lines + i;
			if (ld->start.y == l) {
				line[ld->start.x] = '#';
				memset(line + ld->start.x + 1, 'X', ld->len - 2);
				line[ld->start.x + ld->len - 1] = '#';
			}
		}
		if (l % 1000 == 0 || line_count <= 1000) {
			enum print_state {
				ps_def, ps_green, ps_red, ps_mark
			} ps = ps_def;
			for (idx c = 0; c < line_len; c += (line_len > 1000 ? 1000 : 1)) {
				if (line[c] == '#') {
					if (ps != ps_red) {
						ps = ps_red;
						fputs(FC_RED, str);
					}
				} else if (min && l >= min->y && l <= max->y && c >= min->x
						&& c <= max->x) {
					if (ps != ps_mark) {
						ps = ps_mark;
						fputs(FC_CYAN, str);
					}
				} else if (line[c] == 'X') {
					if (ps != ps_green) {
						ps = ps_green;
						fputs(FC_GREEN, str);
					}
				} else if (line[c] == '.') {
					if (ps != ps_def) {
						ps = ps_def;
						fputs(RESET, str);
					}
				} else
					abort();
				fputc(line[c], str);
			}
			if (ps != ps_def)
				fputs(RESET"\n", str);
			else
				fputc('\n', str);
		}
		for (idx i = 0; i < (data->pos_count >> 1); ++i) {
			struct line2d *ld = lines + i;
			if (ld->start.y == l) {
				if (ld->len < 3)
					abort();
				if (lastline[ld->start.x + 1] == '.') {
					line[ld->start.x] = 'X';
					line[ld->start.x + ld->len - 1] = 'X';
				} else {
					memset(line + ld->start.x, '.', ld->len);
					if (ld->start.x && lastline[ld->start.x - 1] != '.')
						line[ld->start.x] = 'X';
					if (ld->start.x + ld->len != line_len
							&& lastline[ld->start.x + ld->len] != '.')
						line[ld->start.x + ld->len - 1] = 'X';
				}
			}
		}
		memcpy(lastline, line, line_len);
	}
	fputs(interactive ? STEP_FINISHED : RESET, str);
}

const char* solve(const char *path) {
	struct data *data = read_data(path);
	uint64_t result = 0;
	if (data->pos_count & 1)
		abort();
	struct line2d *lines = malloc(sizeof(struct line2d) * data->pos_count >> 1);
	for (idx i = 0; i < data->pos_count; i += 2) {
		struct pos2d p = data->positions[i];
		struct pos2d o = data->positions[i + 1];
		if (p.y != o.y)
			o = data->positions[i ? i - 1 : data->pos_count - 1];
		lines[i >> 1].start.y = p.y;
		if (p.x < o.x) {
			lines[i >> 1].start.x = p.x;
			lines[i >> 1].len = o.x - p.x + 1;
		} else {
			lines[i >> 1].start.x = o.x;
			lines[i >> 1].len = p.x - o.x + 1;
		}
	}
	print(solution_out, data, result, lines, NULL, NULL);
	for (idx mini = 0; mini + 1 < data->pos_count; ++mini) {
		struct pos2d minp = data->positions[mini];
		for (idx maxi = mini + 1; maxi < data->pos_count; ++maxi) {
			struct pos2d maxp = data->positions[maxi];
			size_t area;
			if (maxp.y > minp.y)
				area = maxp.y - minp.y + 1;
			else
				area = minp.y - maxp.y + 1;
			if (maxp.x > minp.x)
				area *= maxp.x - minp.x + 1;
			else
				area *= minp.x - maxp.x + 1;
			if (area <= result)
				continue;
			struct pos2d p = { minp.x < maxp.x ? minp.x : maxp.x,
					minp.y < maxp.y ? minp.y : maxp.y };
			struct pos2d endp = { minp.x > maxp.x ? minp.x : maxp.x,
					minp.y > maxp.y ? minp.y : maxp.y };
			if (part == 2) {
				//(94654,50355) and (5556,67344) = 1513792010
				if (p.x == 5556 && p.y == 50355
						&& endp.x == 94654 && endp.y == 67344)
					fputs("BREAK!\n", stderr);
				_Bool is_inside = 0;
				for (idx i = 0; i < (data->pos_count >> 1); ++i) {
					if (lines[i].start.y <= p.y) {
						pos1d start = lines[i].start.x;
						pos1d end = lines[i].start.x + lines[i].len - 1;
						if (start <= p.x && end > p.x)
							is_inside ^= 1;
					}
					if (lines[i].start.y <= p.y || lines[i].start.y >= endp.y)
						continue;
					if (lines[i].start.x >= endp.x)
						continue;
					if (lines[i].start.x + lines[i].len - 1 <= p.x)
						continue;
					fputs(RESET, stdout);
					goto inval;
				}
				if (!(is_inside & 1))
					goto inval;
			}
			result = area;
			print(solution_out, data, result, lines, &p, &endp);
			inval: ;
		}
	}
	print(solution_out, data, result, lines, NULL, NULL);
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
