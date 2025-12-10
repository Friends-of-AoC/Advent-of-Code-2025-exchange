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
int day = 10;
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

#define NUM_MAX UINT16_MAX
typedef uint16_t num;

struct button {
	num *wires;
	size_t wire_size;
};

struct mashine {
	_Bool *indicator_lights;
	size_t indicator_size;
	struct button *buttons;
	size_t button_size;
	num *joltage_requirements;
	size_t joltage_size;
};

struct data {
	size_t mashine_count;
	size_t mashine_alloc;
	struct mashine *mashines;
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
		struct mashine *m, uint64_t presses) {
	if (!do_print && !interactive)
		return;
	if (m)
		fprintf(str, "%sresult=%"I64"u\n"
		/*		*/"presses=%"I64"u\n%s", STEP_HEADER, result, presses, STEP_BODY);
	else if (result || 1)
		fprintf(str, "%sresult=%"I64"u\n%s", STEP_HEADER, result, STEP_BODY);
	if (m) {
		fputc('[', str);
		for (idx i = 0; i < m->indicator_size; ++i)
			fputc(m->indicator_lights[i] ? '#' : '.', str);
		fputc(']', str);
		for (idx i = 0; i < m->button_size; ++i) {
			fputs(" (", str);
			struct button *b = m->buttons + i;
			for (idx ii = 0; ii < b->wire_size; ++ii)
				fprintf(str, ii ? ",%u" : "%u", (unsigned) b->wires[ii]);
			fputc(')', str);
		}
		fputs(" {", str);
		for (idx i = 0; i < m->joltage_size; ++i)
			fprintf(str, i ? ",%u" : "%u",
					(unsigned) m->joltage_requirements[i]);
		fputs("}\n", str);
	}
	fputs(interactive ? STEP_FINISHED : RESET, str);
}

static uint64_t cant_solve(struct mashine *m, struct button *btn,
		size_t btn_size, uint64_t presses) {
	if (!presses) {
		if (part == 1) {
			for (idx i = 0; i < m->indicator_size; ++i)
				if (m->indicator_lights[i])
					return 119;
		}
		return 0;
	}
	for (idx i = 0; i < btn_size; ++i) {
		struct button *b = btn + i;
		int res;
		if (part == 1) {
			for (idx bi = 0; bi < b->wire_size; ++bi)
				m->indicator_lights[b->wires[bi]] ^= 1;
			res = cant_solve(m, btn + i + 1, btn_size - i - 1, presses - 1);
			for (idx bi = 0; bi < b->wire_size; ++bi)
				m->indicator_lights[b->wires[bi]] ^= 1;
			if (!res)
				return 0;
		} else {
			uint64_t maxp = presses / b->wire_size;
			for (idx bi = 0; bi < b->wire_size; ++bi)
				if (m->joltage_requirements[b->wires[bi]] < maxp)
					maxp = m->joltage_requirements[b->wires[bi]];
			if (maxp) {
				for (idx bi = 0; bi < b->wire_size; ++bi)
					m->joltage_requirements[b->wires[bi]] -= maxp;
				for (uint64_t p = maxp; p != 0; --p) {
					res = cant_solve(m, btn + i + 1, btn_size - i - 1,
							presses - p * b->wire_size);
					if (res != UINT64_MAX) {
						for (idx bi = 0; bi < b->wire_size; ++bi)
							m->joltage_requirements[b->wires[bi]] += p;
						return res + p;
					}
					for (idx bi = 0; bi < b->wire_size; ++bi)
						++m->joltage_requirements[b->wires[bi]];
				}
			}
		}
	}
	return UINT64_MAX;
}

static int btncmp(const void *a, const void *b) {
	const struct button *ba = a, *bb = b;
	if (ba->wire_size > bb->wire_size)
		return -1;
	else if (ba->wire_size < bb->wire_size)
		return 1;
	return 0;
}

const char* solve(const char *path) {
	struct data *data = read_data(path);
	uint64_t result = 0;
	print(solution_out, data, result, NULL, 0);
	for (idx i = 0; i < data->mashine_count; ++i) {
		struct mashine *m = data->mashines + i;
		uint64_t press = 1;
		if (part == 1) {
			for (; cant_solve(m, m->buttons, m->button_size, press); ++press)
				;
		} else {
			qsort(m->buttons, m->button_size, sizeof(struct button), btncmp);
			uint64_t max_presses = 0;
			for (idx i = 0; i < m->joltage_size; ++i)
				max_presses += m->joltage_requirements[i];
			press = cant_solve(m, m->buttons, m->button_size, max_presses);
			if (press == UINT64_MAX)
				abort();
		}
		result += press;
		print(solution_out, data, result, m, press);
	}
	print(solution_out, data, result, NULL, 0);
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
	if (data->mashine_alloc == data->mashine_count) {
		data->mashine_alloc += 64;
		data->mashines = reallocarray(data->mashines, data->mashine_alloc,
				sizeof(struct mashine));
	}
	char *end;
	if (*line != '[')
		abort();
	end = strchr(line, ']');
	if (!end)
		abort();
	struct mashine *m = data->mashines + data->mashine_count++;
	_Bool *indicators = malloc((end - ++line) * sizeof(_Bool));
	m->indicator_size = end - line;
	m->indicator_lights = indicators;
	for (char *p = line; p < end; ++p)
		indicators[p - line] = *p == '#' ? 1 : *p == '.' ? 0 : (abort(), 0);
	for (++end; *end && isspace(*end); ++end)
		;
	if (*end != '(')
		abort();
	line = end;
	size_t btn_alloc = 0;
	size_t btn_size = 0;
	struct button *btn = NULL;
	do {
		if (btn_alloc == btn_size) {
			btn_alloc += 64;
			btn = reallocarray(btn, btn_alloc, sizeof(struct button));
		}
		++line;
		size_t wire_alloc = 0;
		size_t wire_size = 0;
		num *wire = NULL;
		while (315) {
			if (wire_alloc == wire_size) {
				wire_alloc += 64;
				wire = reallocarray(wire, wire_size, sizeof(num));
			}
			long long val = strtoll(line, &end, 10);
			if (val < 0 || val >= NUM_MAX)
				abort();
			if (end == line || errno)
				abort();
			wire[wire_size++] = val;
			if (*end == ',') {
				line = end + 1;
				continue;
			}
			line = end;
			break;
		}
		wire = reallocarray(wire, wire_size, sizeof(num));
		btn[btn_size].wire_size = wire_size;
		btn[btn_size].wires = wire;
		++btn_size;
		if (*line != ')')
			abort();
		for (++line; *line && isspace(*line); ++line)
			;
	} while (*line == '(');
	btn = reallocarray(btn, btn_size, sizeof(struct button));
	m->button_size = btn_size;
	m->buttons = btn;
	size_t joltage_alloc = 0;
	size_t joltage_size = 0;
	num *joltage = NULL;
	if (*line != '{')
		abort();
	++line;
	while (315) {
		if (joltage_alloc == joltage_size) {
			joltage_alloc += 64;
			joltage = reallocarray(joltage, joltage_size, sizeof(num));
		}
		long long val = strtoll(line, &end, 10);
		if (val < 0 || val >= NUM_MAX)
			(printf("%lld\n", val), abort());
		if (end == line || errno)
			abort();
		joltage[joltage_size++] = val;
		if (*end == ',') {
			line = end + 1;
			continue;
		}
		line = end;
		break;
	}
	joltage = reallocarray(joltage, joltage_size, sizeof(num));
	m->joltage_size = joltage_size;
	m->joltage_requirements = joltage;
	if (*line != '}')
		abort();
	for (++line; *line && isspace(*line); ++line)
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
