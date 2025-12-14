using System.Globalization;

namespace Y25Day02
{
    /// <summary>
    /// Represents a range of ids
    /// </summary>
    public sealed class IdRange
    {
        /// <summary>
        /// The dash separator
        /// </summary>
        public const char Seperator = '-';

        /// <summary>
        /// The starting value
        /// </summary>
        public long StartingValue { get; }

        /// <summary>
        /// The ending value
        /// </summary>
        public long EndingValue { get; }

        /// <summary>
        /// Creates a new instance of <see cref="IdRange"/>
        /// </summary>
        /// <param name="startingValue">The starting value</param>
        /// <param name="endingValue">The ending value</param>
        public IdRange(long startingValue, long endingValue) : base()
        {
            ArgumentOutOfRangeException.ThrowIfNegative(startingValue);

            ArgumentOutOfRangeException.ThrowIfNegative(endingValue);

            ArgumentOutOfRangeException.ThrowIfGreaterThanOrEqual(startingValue, endingValue);

            StartingValue = startingValue;

            EndingValue = endingValue;
        }

        /// <summary>
        /// <inheritdoc/>
        /// </summary>
        /// <returns></returns>
        public override string ToString() => $"{StartingValue} {Seperator} {EndingValue}";

        /// <summary>
        /// Creates and returns a <see cref="IdRange"/> from the specified <paramref name="stringRepresentation"/>
        /// </summary>
        /// <param name="stringRepresentation">The string representation</param>
        /// <returns></returns>
        public static IdRange Create(string stringRepresentation)
        {
            ArgumentException.ThrowIfNullOrWhiteSpace(stringRepresentation);

            var parts = stringRepresentation.Split(Seperator, StringSplitOptions.TrimEntries);

            var startingValue = long.Parse(parts[0], CultureInfo.InvariantCulture);

            var endingValue = long.Parse(parts[1], CultureInfo.InvariantCulture);

            return new(startingValue, endingValue);
        }

        /// <summary>
        /// Returns the ids contained in the range
        /// </summary>
        /// <returns></returns>
        public IEnumerable<long> GetRangeIds()
        {
            return GenerateSequence(StartingValue, EndingValue);
        }

        /// <summary>
        /// Generates a sequence from the <paramref name="start"/> to the <paramref name="end"/> inclusive.
        /// </summary>
        /// <param name="start">The starting value</param>
        /// <param name="end">The ending value</param>
        /// <returns></returns>
        private static IEnumerable<long> GenerateSequence(long start, long end)
        {
            for (var i = start; i <= end; i++)
            {
                yield return i;
            }
        }
    }
}