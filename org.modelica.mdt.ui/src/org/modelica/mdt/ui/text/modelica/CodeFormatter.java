/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.modelica.mdt.ui.text.modelica;

import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.TextEdit;

/**
 * Specification for a generic source code formatter.
 *
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class CodeFormatter {

	/**
	 * Unknown kind
	 *
	 * Note that since 3.4, the {@link #F_INCLUDE_COMMENTS} flag can be added
	 * to this constant in order to get the comments formatted if a compilation unit
	 * is processed.
	 */
	public static final int K_UNKNOWN = 0x00;

	/**
	 * Kind used to format an expression
	 */
	public static final int K_EXPRESSION = 0x01;

	/**
	 * Kind used to format a set of statements
	 */
	public static final int K_STATEMENTS = 0x02;

	/**
	 * Kind used to format a set of class body declarations
	 */
	public static final int K_CLASS_BODY_DECLARATIONS = 0x04;

	/**
	 * Kind used to format a compilation unit
	 * <p>
	 * Note that using this constant, the comments are only indented while
	 * processing the compilation unit.
	 * </p><p>
	 * <b>Since 3.4</b>, if the corresponding comment option is set to
	 * <code>true</code> then it is also possible to format the comments on the fly
	 * by adding the {@link #F_INCLUDE_COMMENTS} flag to this kind of format.
	 */
	public static final int K_COMPILATION_UNIT = 0x08;

	/**
	 * Kind used to format a single-line comment
	 * @since 3.1
	 */
	public static final int K_SINGLE_LINE_COMMENT = 0x10;

	/**
	 * Kind used to format a multi-line comment
	 * @since 3.1
	 */
	public static final int K_MULTI_LINE_COMMENT = 0x20;

	/**
	 * Kind used to format a Javadoc comment
	 *
	 * @since 3.1
	 */
	public static final int K_JAVA_DOC = 0x40;

	/**
	 * Flag used to include the comments during the formatting of the code
	 * snippet.
	 * <p>
	 * This flag can only be combined with {@link #K_COMPILATION_UNIT} and
	 * {@link #K_UNKNOWN} kinds for now but might be extended to other ones
	 * in future versions.
	 * </p><p>
	 * Note also that it has an effect only when the option
	 * {@link DefaultCodeFormatterConstants#FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT}
	 * is set to {@link DefaultCodeFormatterConstants#TRUE} while calling
	 * {@link #format(int, String, int, int, int, String)} or
	 * {@link #format(int, String, IRegion[], int, String)} methods
	 * </p><p>
	 * For example, with the Eclipse default formatter options, the formatting of
	 * the following code snippet using {@link #K_COMPILATION_UNIT}:
	 * <pre>
	 * public class X {
	 * &#047;&#042;&#042;
	 *  &#042; This is just a simple example to show that comments will be formatted while processing a compilation unit only if the constant flag <code>F_INCLUDE_COMMENT</code> flag is set.
	 *  &#042; &#064;param str The input string
	 *  &#042;&#047;
	 *  void foo(String str){}}
	 * </pre>
	 * will produce the following output:
	 * <pre>
	 * public class X {
	 * 	&#047;&#042;&#042;
	 *  	 &#042; This is just a simple example to show that comments will be formatted while processing a compilation unit only if the constant flag <code>F_INCLUDE_COMMENT</code> flag is set.
	 *  	 &#042;
	 *  	 &#042; &#064;param str The input string
	 *  	 &#042;&#047;
	 *  	void foo(String str){
	 *  	}
	 *  }
	 * </pre>
	 * Adding this flavor to the kind given while formatting the same source
	 * (e.g. {@link #K_COMPILATION_UNIT} | {@link #F_INCLUDE_COMMENTS})
	 * will produce the following output instead:
	 * <pre>
	 * public class X {
	 * 	&#047;&#042;&#042;
	 *  	 &#042; This is just a simple example to show that comments will be formatted
	 *  	 &#042; while processing a compilation unit only if the constant flag
	 *  	 &#042; <code>F_INCLUDE_COMMENT</code> flag is set.
	 *  	 &#042;
	 *  	 &#042; &#064;param str
	 *  	 &#042; 		The input string
	 *  	 &#042;&#047;
	 *  	void foo(String str){
	 *  	}
	 *  }
	 * </pre>
	 * </p><p>
	 * <i><u>Note</u>: Although we're convinced that the formatter should
	 * always include the comments while processing a
	 * {@link #K_COMPILATION_UNIT kind of compilation unit}, we
	 * have decided to add a specific flag to fix this formatter incorrect behavior.
	 * This will prevent all existing clients (e.g. 3.3 and previous versions) using
	 * the {@link #K_COMPILATION_UNIT} kind to be broken while formatting.</i>
	 * </p>
	 * @since 3.4
	 */
	public static final int F_INCLUDE_COMMENTS = 0x1000;

	/**
	 * Format <code>source</code>,
	 * and returns a text edit that correspond to the difference between the given
	 * string and the formatted string.
	 * <p>
	 * It returns null if the given string cannot be formatted.
	 * </p><p>
	 * If the offset position is matching a whitespace, the result can include
	 * whitespaces. It would be up to the caller to get rid of preceding
	 * whitespaces.
	 * </p>
	 *
	 * @param kind Use to specify the kind of the code snippet to format. It can
	 * be any of these:
	 * <ul>
	 * 	<li>{@link #K_EXPRESSION}</li>
	 * 	<li>{@link #K_STATEMENTS}</li>
	 * 	<li>{@link #K_CLASS_BODY_DECLARATIONS}</li>
	 * 	<li>{@link #K_COMPILATION_UNIT}<br>
	 * 		<b>Since 3.4</b>, the comments can be formatted on the fly while
	 * 		using this kind of code snippet<br>
	 * 		(see {@link #F_INCLUDE_COMMENTS} for more detailed explanation on
	 * 		this flag)
	 * 	</li>
	 * 	<li>{@link #K_UNKNOWN}</li>
	 * 	<li>{@link #K_SINGLE_LINE_COMMENT}</li>
	 * 	<li>{@link #K_MULTI_LINE_COMMENT}</li>
	 * 	<li>{@link #K_JAVA_DOC}</li>
	 * </ul>
	 * @param source the source to format
	 * @param offset the given offset to start recording the edits (inclusive).
	 * @param length the given length to stop recording the edits (exclusive).
	 * @param indentationLevel the initial indentation level, used
	 *      to shift left/right the entire source fragment. An initial indentation
	 *      level of zero or below has no effect.
	 * @param lineSeparator the line separator to use in formatted source,
	 *     if set to <code>null</code>, then the platform default one will be used.
	 * @return the text edit
	 * @throws IllegalArgumentException if offset is lower than 0, length is lower than 0 or
	 * length is greater than source length.
	 */
	public abstract TextEdit format(int kind, String source, int offset, int length, int indentationLevel, String lineSeparator);

	/**
	 * Format <code>source</code>,
	 * and returns a text edit that correspond to the difference between the given string and the formatted string.
	 * <p>It returns null if the given string cannot be formatted.</p>
	 *
	 * <p>If an offset position is matching a whitespace, the result can include whitespaces. It would be up to the
	 * caller to get rid of preceding whitespaces.</p>
	 *
	 * <p>No region in <code>regions</code> must overlap with any other region in <code>regions</code>.
	 * Each region must be within source. There must be at least one region. Regions must be sorted
	 * by their offsets, smaller offset first.</p>
	 *
	 * @param kind Use to specify the kind of the code snippet to format. It can
	 * be any of these:
	 * <ul>
	 * 	<li>{@link #K_EXPRESSION}</li>
	 * 	<li>{@link #K_STATEMENTS}</li>
	 * 	<li>{@link #K_CLASS_BODY_DECLARATIONS}</li>
	 * 	<li>{@link #K_COMPILATION_UNIT}<br>
	 * 		<b>Since 3.4</b>, the comments can be formatted on the fly while
	 * 		using this kind of code snippet<br>
	 * 		(see {@link #F_INCLUDE_COMMENTS} for more detailed explanation on
	 * 		this flag)
	 * 	</li>
	 * 	<li>{@link #K_UNKNOWN}</li>
	 * 	<li>{@link #K_SINGLE_LINE_COMMENT}</li>
	 * 	<li>{@link #K_MULTI_LINE_COMMENT}</li>
	 * 	<li>{@link #K_JAVA_DOC}</li>
	 * </ul>
	 * @param source the source to format
	 * @param regions a set of regions in source to format
	 * @param indentationLevel the initial indentation level, used
	 *      to shift left/right the entire source fragment. An initial indentation
	 *      level of zero or below has no effect.
	 * @param lineSeparator the line separator to use in formatted source,
	 *     if set to <code>null</code>, then the platform default one will be used.
	 * @return the text edit
	 * @throws IllegalArgumentException if there is no region, a region overlaps with another region, or the regions are not
	 * sorted in the ascending order.
	 * @since 3.4
	 */
	public abstract TextEdit format(int kind, String source, IRegion[] regions, int indentationLevel, String lineSeparator);

	/**
	 * Answers the string that corresponds to the indentation to the given indentation level or an empty string
	 * if the indentation cannot be computed.
	 * <p>This method needs to be overridden in a subclass.</p>
	 *
	 * <p>The default implementation returns an empty string.</p>
	 *
	 * @param indentationLevel the given indentation level
	 * @return the string corresponding to the right indentation level
	 * @exception IllegalArgumentException if the given indentation level is lower than zero
	 * @since 3.2
	 */
	public String createIndentationString(int indentationLevel) {
		return "";
	}
}
