/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Christopher Lenz (cmlenz@gmx.de) - support for line continuation
 *******************************************************************************/
package org.modelica.mdt.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Standard implementation of <code>IPredicateRule</code>. Is is capable of
 * detecting a pattern which begins with a given start sequence and ends with a
 * given List of end sequences. If no end sequence is not specified, it can be
 * either end of line, end or file, or both. Additionally, the pattern can be
 * constrained to begin in a certain column. The rule can also be used to check
 * whether the text to scan covers half of the pattern, i.e. contains the end
 * sequence required by the rule.
 */
public class ExtendedMultiLineRule implements IPredicateRule
{

	/**
	 * Comparator that orders <code>char[]</code> in decreasing array lengths.
	 * 
	 * @since 3.1
	 */
	private static class DecreasingCharArrayLengthComparator implements Comparator<char[]>
	{
		public int compare(char[] o1, char[] o2)
		{
			return o2.length - o1.length;
		}
	}

	/** Internal setting for the un-initialized column constraint */
	protected static final int UNDEFINED = -1;

	/**
	 * If the start Pattern is found multiple times this counter will Increment,
	 * so that you stop at the correct ending sequence
	 */

	/** The token to be returned on success */
	protected IToken fToken;
	/** The pattern's start sequence */
	protected char[] fStartSequence;
	/** The pattern's end sequence */
	protected ArrayList<char[]> fEndSequence;
	/** The pattern's second end sequence */
	// protected char[] fEndSequence2;
	/** The pattern's column constrain */
	protected int fColumn = UNDEFINED;
	/** The pattern's escape character */
	protected char fEscapeCharacter;
	/**
	 * Indicates whether the escape character continues a line
	 * 
	 * @since 3.0
	 */
	protected boolean fEscapeContinuesLine;
	/** Indicates whether end of line terminates the pattern */
	protected boolean fBreaksOnEOL;
	/** Indicates whether end of file terminates the pattern */
	protected boolean fBreaksOnEOF;

	/**
	 * Line delimiter comparator which orders according to decreasing delimiter
	 * length.
	 * 
	 * @since 3.1
	 */
	private Comparator<char[]> fLineDelimiterComparator = new DecreasingCharArrayLengthComparator();
	/**
	 * Cached line delimiters.
	 * 
	 * @since 3.1
	 */
	private char[][] fLineDelimiters;
	/**
	 * Cached sorted {@linkplain #fLineDelimiters}.
	 * 
	 * @since 3.1
	 */
	private char[][] fSortedLineDelimiters;

	/**
	 * Creates a rule for the given starting and ending sequence. When these
	 * sequences are detected the rule will return the specified token.
	 * Alternatively, the sequence can also be ended by the end of the line. Any
	 * character which follows the given escapeCharacter will be ignored.
	 * 
	 * @param startSequence
	 *            the pattern's start sequence
	 * @param endSequence
	 *            the pattern's end sequence, <code>null</code> is a legal value
	 * @param token
	 *            the token which will be returned on success
	 * @param escapeString
	 *            any character following this one will be ignored
	 * @param breaksOnEOL
	 *            indicates whether the end of the line also terminates the
	 *            pattern
	 */
	public ExtendedMultiLineRule(String startSequence, ArrayList<String> endSequence, IToken token, char escapeCharacter, boolean breaksOnEOL)
	{
		Assert.isTrue(startSequence != null && startSequence.length() > 0);
		Assert.isTrue(endSequence != null || breaksOnEOL);
		Assert.isNotNull(token);

		fStartSequence = startSequence.toCharArray();
		// fEndSequence= (endSequence == null ? new char[0] :
		// endSequence.toCharArray());
		// fEndSequence2= (endSequence2 == null ? new char[0] :
		// endSequence2.toCharArray());
		fEndSequence = new ArrayList<char[]>();
		for (Iterator<String> i = endSequence.iterator(); i.hasNext();) {
			fEndSequence.add(i.next().toCharArray());
		}

		fToken = token;
		fEscapeCharacter = escapeCharacter;
		fBreaksOnEOL = breaksOnEOL;
	}

	/**
	 * Creates a rule for the given starting and ending sequence. When these
	 * sequences are detected the rule will return the specified token.
	 * Alternatively, the sequence can also be ended by the end of the line or
	 * the end of the file. Any character which follows the given
	 * escapeCharacter will be ignored.
	 * 
	 * @param startSequence
	 *            the pattern's start sequence
	 * @param endSequence
	 *            the pattern's end sequence, <code>null</code> is a legal value
	 * @param token
	 *            the token which will be returned on success
	 * @param escapeCharacter
	 *            any character following this one will be ignored
	 * @param breaksOnEOL
	 *            indicates whether the end of the line also terminates the
	 *            pattern
	 * @param breaksOnEOF
	 *            indicates whether the end of the file also terminates the
	 *            pattern
	 * @since 2.1
	 */
	public ExtendedMultiLineRule(String startSequence, ArrayList<String> endSequence, IToken token, char escapeCharacter, boolean breaksOnEOL, boolean breaksOnEOF)
	{
		this(startSequence, endSequence, token, escapeCharacter, breaksOnEOL);
		fBreaksOnEOF = breaksOnEOF;
	}

	/**
	 * Creates a rule for the given starting and ending sequence. When these
	 * sequences are detected the rule will return the specified token.
	 * Alternatively, the sequence can also be ended by the end of the line or
	 * the end of the file. Any character which follows the given
	 * escapeCharacter will be ignored. An end of line immediately after the
	 * given <code>lineContinuationCharacter</code> will not cause the pattern
	 * to terminate even if <code>breakOnEOL</code> is set to true.
	 * 
	 * @param startSequence
	 *            the pattern's start sequence
	 * @param endSequence
	 *            the pattern's end sequence, <code>null</code> is a legal value
	 * @param token
	 *            the token which will be returned on success
	 * @param escapeCharacter
	 *            any character following this one will be ignored
	 * @param escapeString
	 *            a second endSequence
	 * @param breaksOnEOL
	 *            indicates whether the end of the line also terminates the
	 *            pattern
	 * @param breaksOnEOF
	 *            indicates whether the end of the file also terminates the
	 *            pattern
	 * @param escapeContinuesLine
	 *            indicates whether the specified escape character is used for
	 *            line continuation, so that an end of line immediately after
	 *            the escape character does not terminate the pattern, even if
	 *            <code>breakOnEOL</code> is set
	 * @since 3.0
	 */
	public ExtendedMultiLineRule(String startSequence, ArrayList<String> endSequence, IToken token, char escapeCharacter, boolean breaksOnEOL, boolean breaksOnEOF, boolean escapeContinuesLine)
	{
		this(startSequence, endSequence, token, escapeCharacter, breaksOnEOL, breaksOnEOF);
		fEscapeContinuesLine = escapeContinuesLine;
	}

	/**
	 * Sets a column constraint for this rule. If set, the rule's token will
	 * only be returned if the pattern is detected starting at the specified
	 * column. If the column is smaller then 0, the column constraint is
	 * considered removed.
	 * 
	 * @param column
	 *            the column in which the pattern starts
	 */
	public void setColumnConstraint(int column)
	{
		if (column < 0)
			column = UNDEFINED;
		fColumn = column;
	}

	/**
	 * Evaluates this rules without considering any column constraints.
	 * 
	 * @param scanner
	 *            the character scanner to be used
	 * @return the token resulting from this evaluation
	 */
	protected IToken doEvaluate(ICharacterScanner scanner)
	{
		return doEvaluate(scanner, false);
	}

	/**
	 * Evaluates this rules without considering any column constraints. Resumes
	 * detection, i.e. look sonly for the end sequence required by this rule if
	 * the <code>resume</code> flag is set.
	 * 
	 * @param scanner
	 *            the character scanner to be used
	 * @param resume
	 *            <code>true</code> if detection should be resumed,
	 *            <code>false</code> otherwise
	 * @return the token resulting from this evaluation
	 * @since 2.0
	 */
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume)
	{

		if (resume) {

			if (endSequenceDetected(scanner))
				return fToken;

		} else {

			int c = scanner.read();
			if (c == fStartSequence[0]) {
				if (sequenceDetected(scanner, fStartSequence, false)) {
					if (endSequenceDetected(scanner))
						return fToken;
				}
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		return evaluate(scanner, false);
	}

	/**
	 * Returns whether the end sequence was detected. As the pattern can be
	 * considered ended by a line delimiter, the result of this method is
	 * <code>true</code> if the rule breaks on the end of the line, or if the
	 * EOF character is read.
	 * 
	 * @param scanner
	 *            the character scanner to be used
	 * @return <code>true</code> if the end sequence has been detected
	 */
	protected boolean endSequenceDetected(ICharacterScanner scanner)
	{

		char[][] originalDelimiters = scanner.getLegalLineDelimiters();
		int count = originalDelimiters.length;
		if (fLineDelimiters == null || fLineDelimiters.length != count) {
			fSortedLineDelimiters = new char[count][];
		} else {
			while (count > 0 && Arrays.equals(fLineDelimiters[count - 1], originalDelimiters[count - 1]))
				count--;
		}
		if (count != 0) {
			fLineDelimiters = originalDelimiters;
			System.arraycopy(fLineDelimiters, 0, fSortedLineDelimiters, 0, fLineDelimiters.length);
			Arrays.sort(fSortedLineDelimiters, fLineDelimiterComparator);
		}

		int readCount = 1;
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip escaped character(s)
				if (fEscapeContinuesLine) {
					c = scanner.read();
					for (int i = 0; i < fSortedLineDelimiters.length; i++) {
						if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], fBreaksOnEOF))
							break;
					}
				} else
					scanner.read();

			} /*
			 * else if (fEndSequence.length > 0 && c == fEndSequence[0]) { //
			 * Check if the specified end sequence has been found. if
			 * (sequenceDetected(scanner, fEndSequence, fBreaksOnEOF)) return
			 * true;
			 */
			else if (!(fEndSequence.isEmpty())) {
				for (Iterator<char[]> i = fEndSequence.iterator(); i.hasNext();) {
					char[] item = i.next();
					if (item.length > 0 && c == item[0])
						if (sequenceDetected(scanner, item, fBreaksOnEOF))
							return true;
				}

			}

			else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the
				// pattern.
				for (int i = 0; i < fSortedLineDelimiters.length; i++) {
					if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], fBreaksOnEOF))
						return true;
				}
			}
			readCount++;
		}

		if (fBreaksOnEOF)
			return true;

		for (; readCount > 0; readCount--)
			scanner.unread();

		return false;
	}

	/**
	 * Returns whether the next characters to be read by the character scanner
	 * are an exact match with the given sequence. No escape characters are
	 * allowed within the sequence. If specified the sequence is considered to
	 * be found when reading the EOF character.
	 * 
	 * @param scanner
	 *            the character scanner to be used
	 * @param sequence
	 *            the sequence to be detected
	 * @param eofAllowed
	 *            indicated whether EOF terminates the pattern
	 * @return <code>true</code> if the given sequence has been detected
	 */
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
	{
		for (int i = 1; i < sequence.length; i++) {
			int c = scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (c != sequence[i]) {
				// Non-matching character detected, rewind the scanner back to
				// the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j = i - 1; j > 0; j--)
					scanner.unread();
				return false;
			}
		}

		return true;
	}

	/*
	 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
	 * 
	 * @since 2.0
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		if (fColumn == UNDEFINED)
			return doEvaluate(scanner, resume);

		int c = scanner.read();
		scanner.unread();
		if (c == fStartSequence[0])
			return (fColumn == scanner.getColumn() ? doEvaluate(scanner, resume) : Token.UNDEFINED);
		return Token.UNDEFINED;
	}

	/*
	 * @see IPredicateRule#getSuccessToken()
	 * 
	 * @since 2.0
	 */
	public IToken getSuccessToken()
	{
		return fToken;
	}
}
