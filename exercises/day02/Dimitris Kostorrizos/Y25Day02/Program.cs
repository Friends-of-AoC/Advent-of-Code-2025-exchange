using System.Globalization;

namespace Y25Day02
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

            var fileContent = await File.ReadAllTextAsync(fileName);

            var idRanges = fileContent.Split(',', StringSplitOptions.TrimEntries);

            var invalidIds = new List<long>();

            foreach (var idRange in idRanges)
            {
                var range = IdRange.Create(idRange);

                var rangeIds = range.GetRangeIds();

                invalidIds.AddRange(rangeIds.Where(x => !IsIdValidForFirstHalf(x)));
            }

            var sum = invalidIds.Sum();

            Console.WriteLine($"The solution is {sum}. Hope you liked it. Press any key to close the console.");

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

            var fileContent = await File.ReadAllTextAsync(fileName);

            var idRanges = fileContent.Split(',', StringSplitOptions.TrimEntries);

            var invalidIds = new List<long>();

            foreach (var idRange in idRanges)
            {
                var range = IdRange.Create(idRange);

                var rangeIds = range.GetRangeIds();

                invalidIds.AddRange(rangeIds.Where(x => !IsIdValidForSecondHalf(x)));
            }

            var sum = invalidIds.Sum();

            Console.WriteLine($"The solution is {sum}. Hope you liked it. Press any key to close the console.");

            Console.Read();
        }

        /// <summary>
        /// Returns whether the <paramref name="id"/> is valid, 
        /// as per the specification of the first half if the exercise
        /// </summary>
        /// <param name="id">The id</param>
        /// <returns></returns>
        private static bool IsIdValidForFirstHalf(long id)
        {
            ArgumentOutOfRangeException.ThrowIfNegative(id);

            var stringRepresentation = id.ToString(CultureInfo.InvariantCulture);

            if (stringRepresentation.Length % 2 != 0)
                return true;

            var halfLength = stringRepresentation.Length / 2;

            var firstPart = stringRepresentation.AsSpan(0, halfLength);

            var secondPart = stringRepresentation.AsSpan(halfLength);

            var arePartsDifferent = !firstPart.Equals(secondPart, StringComparison.OrdinalIgnoreCase);

            return arePartsDifferent;
        }

        /// <summary>
        /// Returns whether the <paramref name="id"/> is valid, 
        /// as per the specification of the second half if the exercise
        /// </summary>
        /// <param name="id">The id</param>
        /// <returns></returns>
        private static bool IsIdValidForSecondHalf(long id)
        {
            ArgumentOutOfRangeException.ThrowIfNegative(id);

            var stringRepresentation = id.ToString(CultureInfo.InvariantCulture);

            var possibleSequences = GetPossibleSequences(stringRepresentation);

            foreach (var possibleSequence in possibleSequences)
            {
                var chunks = stringRepresentation.Chunk(possibleSequence.Length)
                                                    .Select(x => new string(x))
                                                    .ToList();

                if (chunks.Count > 1)
                {
                    var areAllChunksMatching = chunks.All(x => x.Equals(possibleSequence, StringComparison.OrdinalIgnoreCase));

                    if(areAllChunksMatching)
                        return false;                    
                }
            }

            return true;
        }

        /// <summary>
        /// Returns all the available sequences that can be extracted
        /// from the starting character of the <paramref name="value"/>
        /// until the half point of the sequence
        /// </summary>
        /// <param name="value">The value</param>
        /// <returns></returns>
        private static IEnumerable<string> GetPossibleSequences(string value)
        {
            ArgumentException.ThrowIfNullOrWhiteSpace(value);

            var halfLength = value.Length / 2;

            for (int i = 1; i <= halfLength; i++)
            {
                yield return value[..i];
            }
        }
    }
}