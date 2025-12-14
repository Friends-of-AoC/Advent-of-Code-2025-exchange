namespace Y25Day06
{
    internal static class Program
    {
        /// <summary>
        /// A  flag indicating whether to use the demo input file or the actual one
        /// </summary>
        private static readonly bool _useDemoFile = false;

        private static async Task Main()
        {
            await ExecuteFirstHalfAsync();

            await ExecuteSecondHalfAsync();
        }

        /// <summary>
        /// Executes the code for the first half of the exercise
        /// </summary>
        /// <returns></returns>
        public static async Task ExecuteFirstHalfAsync()
        {
            var fileName = "Input.txt";

            if (_useDemoFile)
                fileName = "DemoInput.txt";

            var fileContent = await File.ReadAllLinesAsync(fileName);

            var cells = new List<string[]>();

            foreach (var line in fileContent)
            {
                var lineParts = line.Split(Array.Empty<char>(), StringSplitOptions.RemoveEmptyEntries);

                cells.AddRange(lineParts);
            }

            var exercises = new List<Exercise>();

            for (int i = 0; i < cells[0].Length; i++)
            {
                var exerciseParts = cells.Select(x => x[i]).ToArray();

                exercises.Add(Exercise.Create(exerciseParts));
            }

            var resultSum = exercises.Select(x => x.CalculateResult()).Sum();

            Console.WriteLine($"The solution is {resultSum}. Hope you liked it. Press any key to close the console.");

            Console.Read();
        }        

        /// <summary>
        /// Executes the code for the second half of the exercise
        /// </summary>
        /// <returns></returns>
        public static async Task ExecuteSecondHalfAsync()
        {
            var fileName = "Input.txt";

            if (_useDemoFile)
                fileName = "DemoInput.txt";

            var fileContent = await File.ReadAllLinesAsync(fileName);

            var maximumLineLength = fileContent.Max(x => x.Length);

            var exercises = new List<Exercise>();

            var operands = new List<long>();

            var exerciseOperator = Operator.Multiplication;

            for (var index = maximumLineLength - 1; index >= 0; index--)
            {
                Span<char> columnParts = new char[fileContent.Length];

                var rowIndex = 0;

                foreach (var line in fileContent)
                {
                    var element = line.ElementAtOrDefault(index);

                    columnParts[rowIndex] = element;

                    rowIndex++;
                }

                var lastCharacter = columnParts[^1];

                if (Exercise.TryParseOparator(lastCharacter, out var operatorResult))
                {
                    exerciseOperator = operatorResult.Value;

                    columnParts[^1] = ' ';
                }

                if (long.TryParse(columnParts, out var operandResult))
                    operands.Add(operandResult);

                var columnValue = columnParts.Trim();

                if (columnValue.IsEmpty)
                {
                    exercises.Add(new Exercise(operands, exerciseOperator));

                    operands = [];
                }
            }

            if(operands.Count > 0)
                exercises.Add(new Exercise(operands, exerciseOperator));

            var resultSum = exercises.Select(x => x.CalculateResult()).Sum();

            Console.WriteLine($"The solution is {resultSum}. Hope you liked it. Press any key to close the console.");

            Console.Read();
        }
    }
}