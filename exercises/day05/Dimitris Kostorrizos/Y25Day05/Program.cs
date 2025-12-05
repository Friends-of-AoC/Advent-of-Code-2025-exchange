using System.Globalization;

namespace Y25Day05
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

            var fileContent = File.ReadLinesAsync(fileName);

            var freshIdRanges = new List<IdRange>();

            var isIdRange = true;

            var freshIdCount = 0;

            await foreach (var line in fileContent)
            {
                if (string.IsNullOrWhiteSpace(line))
                {
                    isIdRange = false;

                    continue;
                }

                if (isIdRange)
                    freshIdRanges.Add(IdRange.Create(line));
                else
                {
                    var id = long.Parse(line, CultureInfo.InvariantCulture);

                    var isFreshId = freshIdRanges.Any(idRange => idRange.ContainsId(id));

                    if (isFreshId)
                        freshIdCount++;
                }
            }

            Console.WriteLine($"The solution is {freshIdCount}. Hope you liked it. Press any key to close the console.");

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

            var fileContent = File.ReadLinesAsync(fileName);

            var freshIdRanges = new List<IdRange>();

            await foreach (var line in fileContent)
            {
                if (string.IsNullOrWhiteSpace(line))
                    break;

                var idRange = IdRange.Create(line);

                freshIdRanges.Add(idRange);
            }

            var anyMergeOccurred = true;

            while (anyMergeOccurred)
            {
                anyMergeOccurred = false;

                var mergedIdRanges = new List<IdRange>();

                var excludedIdRanges = new List<IdRange>();

                foreach (var first in freshIdRanges)
                {
                    if (excludedIdRanges.Contains(first))
                        continue;

                    foreach (var second in freshIdRanges.Except(excludedIdRanges))
                    {
                        if (excludedIdRanges.Contains(second))
                            continue;

                        if (first == second)
                            continue;

                        if (IdRange.TryMerge(first, second, out var merged))
                        {
                            mergedIdRanges.Add(merged);

                            excludedIdRanges.Add(first);

                            excludedIdRanges.Add(second);

                            anyMergeOccurred = true;
                                
                            break;
                        }
                    }
                }

                if (anyMergeOccurred)
                {
                    freshIdRanges.AddRange(mergedIdRanges);

                    foreach (var excludedIdRange in excludedIdRanges)
                        freshIdRanges.Remove(excludedIdRange);
                }
            }

            var freshIdCount = freshIdRanges.Select(x => x.GetRangeIdCount()).Sum();

            Console.WriteLine($"The solution is {freshIdCount}. Hope you liked it. Press any key to close the console.");

            Console.Read();
        }
    }
}