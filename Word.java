import java.util.Objects;

/**
 * A simple immutable-friendly Java bean representing a word and its definition.
 */
public class Word implements Comparable<Word> {
	private String word;
	private String definition;

	/**
	 * Creates an empty Word with empty strings for fields.
	 */
	public Word() {
		this.word = "";
		this.definition = "";
	}

	/**
	 * Creates a Word with the given word and definition.
	 *
	 * @param word the word text (may be null)
	 * @param definition the word definition (may be null)
	 */
	public Word(String word, String definition) {
		this.word = word;
		this.definition = definition;
	}

	/**
	 * Copy constructor.
	 *
	 * @param other the Word to copy from (may be null)
	 */
	public Word(Word other) {
		if (other == null) {
			this.word = "";
			this.definition = "";
		} else {
			this.word = other.word;
			this.definition = other.definition;
		}
	}

	// Accessors
	public String getWord() {
		return word;
	}

	public String getDefinition() {
		return definition;
	}

	// Mutators
	public void setWord(String word) {
		this.word = word;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * Returns a readable representation of the Word.
	 */
	@Override
	public String toString() {
		return "Word[word=" + word + ", definition=" + definition + "]";
	}

	/**
	 * Compares Words by their `word` field, case-insensitively. Nulls are treated
	 * as empty strings and a null {@code other} is considered less specific and
	 * will return a positive value (this > null).
	 */
	@Override
	public int compareTo(Word other) {
		if (other == null) return 1;
		String a = this.word == null ? "" : this.word;
		String b = other.word == null ? "" : other.word;
		return a.compareToIgnoreCase(b);
	}

	/**
	 * Two Word objects are equal if both `word` and `definition` are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Word)) return false;
		Word other = (Word) obj;
		return Objects.equals(this.word, other.word)
				&& Objects.equals(this.definition, other.definition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(word, definition);
	}
}