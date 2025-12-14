using System.Diagnostics.CodeAnalysis;
using System.Globalization;

namespace Y25Day06
{
    /// <summary>
    /// Represents an exercise
    /// </summary>
    public sealed class Exercise
    {
        /// <summary>
        /// The operands
        /// </summary>
        public IEnumerable<long> Operands { get; }

        /// <summary>
        /// The operator
        /// </summary>
        public Operator Operation { get; }

        /// <summary>
        /// Creates a new instance of <see cref="Exercise"/>
        /// </summary>
        /// <param name="operands">The operands</param>
        /// <param name="operation">The operator</param>
        public Exercise(IEnumerable<long> operands, Operator operation) : base()
        {
            ArgumentNullException.ThrowIfNull(operands);

            Operands = operands;

            Operation = operation;
        }

        /// <summary>
        /// <inheritdoc/>
        /// </summary>
        /// <returns></returns>
        public override string ToString() => $"{Operation}, Arguments: {Operands.Count()}";

        /// <summary>
        /// Creates and returns a <see cref="Exercise"/> from the specified <paramref name="stringRepresentation"/>
        /// </summary>
        /// <param name="stringRepresentation">The string representation</param>
        /// <returns></returns>
        public static Exercise Create(string[] stringRepresentation)
        {
            ArgumentNullException.ThrowIfNull(stringRepresentation);

            Operator operatorValue;

            var lastCell = stringRepresentation[^1];

            if (TryParseOparator(lastCell, out var operatorResult))
                operatorValue = operatorResult.Value;
            else
                throw new InvalidOperationException("The operator must be either '+' or '*'.");

            var operands = stringRepresentation.SkipLast(1)
                .Select(x => long.Parse(x, CultureInfo.InvariantCulture))
                .ToList();

            return new(operands, operatorValue);
        }

        /// <summary>
        /// Calculates the result of the operator
        /// </summary>
        /// <returns></returns>
        public long CalculateResult()
        {
            if (Operation == Operator.Addition)
                return Operands.Sum();

            var result = 1L;

            foreach (var operand in Operands)
                result *= operand;

            return result;
        }

        /// <summary>
        /// Tries to parse the <paramref name="value"/> into a <see cref="Operator"/>.
        /// If the parsing succeeds, the result is returned in the <paramref name="result"/>
        /// </summary>
        /// <param name="value">The value</param>
        /// <param name="result">The result</param>
        /// <returns></returns>
        public static bool TryParseOparator(string value, [NotNullWhen(true)] out Operator? result)
        {
            result = null;

            if (string.IsNullOrWhiteSpace(value))
                return false;

            if (value.Length != 1)
                return false;

            return TryParseOparator(value[0], out result);
        }

        /// <summary>
        /// Tries to parse the <paramref name="value"/> into a <see cref="Operator"/>.
        /// If the parsing succeeds, the result is returned in the <paramref name="result"/>
        /// </summary>
        /// <param name="value">The value</param>
        /// <param name="result">The result</param>
        /// <returns></returns>
        public static bool TryParseOparator(char value, [NotNullWhen(true)] out Operator? result)
        {
            if (value == '+')
            {
                result = Operator.Addition;

                return true;
            }

            if (value == '*')
            {
                result = Operator.Multiplication;

                return true;
            }

            result = null;

            return false;
        }
    }

}