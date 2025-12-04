namespace Y25Day04
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

            var locations = PaperRollLocations.CreateFromFileContent(fileContent);

            var accessibleRollsCount = locations.GetAccessibleRolls().Count();

            Console.WriteLine($"The solution is {accessibleRollsCount}. Hope you liked it. Press any key to close the console.");

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

            var locations = PaperRollLocations.CreateFromFileContent(fileContent);

            var removedRollCount = locations.GetRemovableRollCount();

            Console.WriteLine($"The solution is {removedRollCount}. Hope you liked it. Press any key to close the console.");

            Console.Read();
        }
    }
}