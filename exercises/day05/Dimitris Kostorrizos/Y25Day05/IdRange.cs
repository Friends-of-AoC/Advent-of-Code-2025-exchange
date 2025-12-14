using System.Diagnostics.CodeAnalysis;
using System.Globalization;

namespace Y25Day05
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

            ArgumentOutOfRangeException.ThrowIfGreaterThan(startingValue, endingValue);

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
        /// Tries to merge the <paramref name="first"/> and <paramref name="second"/>, is they overlap
        /// If they overlap, the merged range is returned in the <paramref name="result"/>
        /// </summary>
        /// <param name="first">The first</param>
        /// <param name="second">The second</param>
        /// <param name="result">The merged range</param>
        /// <returns></returns>
        public static bool TryMerge(IdRange first, IdRange second, [NotNullWhen(true)] out IdRange? result)
        {
            ArgumentNullException.ThrowIfNull(first);

            ArgumentNullException.ThrowIfNull(second);

            result = null;

            // If the first is contained in the second...
            if (first.StartingValue >= second.StartingValue && first.EndingValue <= second.EndingValue)
            {
                result = new IdRange(second.StartingValue, second.EndingValue);

                return true;
            }

            // If the second is contained in the first...
            if (second.StartingValue >= first.StartingValue && second.EndingValue <= first.EndingValue)
            {
                result = new IdRange(first.StartingValue, first.EndingValue);

                return true;
            }

            // If first starts from lower numbers but ends in the second...
            if (first.StartingValue < second.StartingValue && 
                second.StartingValue <= first.EndingValue && 
                first.EndingValue <= second.EndingValue)
            {
                result = new IdRange(first.StartingValue, second.EndingValue);

                return true;
            }

            // If second starts from lower numbers but ends in the first...
            if (second.StartingValue < first.StartingValue &&
                first.StartingValue <= second.EndingValue &&
                second.EndingValue <= first.EndingValue)
            {
                result = new IdRange(second.StartingValue, first.EndingValue);

                return true;
            }

            // If first starts in the second but ends in higher numbers...
            if (first.StartingValue >= second.StartingValue &&
                second.EndingValue >= first.StartingValue &&
                first.EndingValue > second.EndingValue)
            {
                result = new IdRange(first.StartingValue, second.EndingValue);

                return true;
            }

            // If second starts in the first but ends in higher numbers...
            if (second.StartingValue >= first.StartingValue &&
                first.EndingValue >= second.StartingValue &&
                second.EndingValue > first.EndingValue)
            {
                result = new IdRange(second.StartingValue, first.EndingValue);

                return true;
            }

            return false;
        }

        /// <summary>
        /// Returns whether the <paramref name="id"/> is contained in the range
        /// </summary>
        /// <param name="id">The id</param>
        /// <returns></returns>
        public bool ContainsId(long id)
            => StartingValue <= id && id<= EndingValue;

        /// <summary>
        /// Returns the count of range ids
        /// </summary>
        /// <returns></returns>
        public long GetRangeIdCount()
            => EndingValue - StartingValue + 1;
    }
}