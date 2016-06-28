package com.ctoddcook.CamGenTools;

import java.io.IOException;

/**
 * Base class for XML importer and exporter.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class XmlBase {
  protected static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  protected static final String INDENT = "  ";


  /**
   * Takes an input string, and searches for any special characters which must be escaped in XML,
   * and if it finds any, replaces them with the escape value.
   *
   * @param input A value which may require escaping
   * @return A string with characters escaped where required
   */
  public static String encodeValue(String input) {
    if (input == null || input.length() == 0)
      return "";

    /*
    In the case of ampersands, we want to replace them with &amp; UNLESS the existing ampersand
    is already part of a escape sequence.
     */
    input = input.replaceAll("&(?!amp;)(?!lt;)(?!gt;)(?!quot;)(?!apos;)", "&amp;");

    /*
    The other replacements are more straight forward.
     */
    input = input.replaceAll("<", "&lt;");
    input = input.replaceAll(">", "&gt;");
    input = input.replaceAll("\"", "&quot;");
    input = input.replaceAll("'", "&apos;");

    return input;
  }

  /**
   * Forms an opening XML tag for the provided tag name. Optionally includes attribute-value pairs.
   *
   * @param name  The tag to be opened
   * @param attrs Optional attribute name-value pairs. There must be an even number of these, with
   *              the name of each attribute followed by its value.
   * @return The formatted opening tag, including optional attribute names and values
   * @throws IOException
   */
  protected String openTag(final String name, final String... attrs) throws IOException {
    final StringBuilder tag = new StringBuilder();

    tag.append('<').append(name);

    if (attrs.length > 0) {
      Assert.isTrue(attrs.length % 2 == 0, "After the tag parameter, parameters must be provided " +
          "in 'name, value' pairs.");
      for (int i = 0; i < attrs.length; i++) {
        tag.append(' ').append(attrs[i++]).append("=\"").append(attrs[i]).append('"');
      }
    }

    tag.append('>');
    return tag.toString();
  }

  /**
   * Forms a closing XML tag for the provided tag name.
   *
   * @param tag The tag to be closed
   * @return A string containing the XML closing tag in braces
   * @throws IOException
   */
  protected String closeTag(final String tag) throws IOException {
    return "</" + tag + '>';
  }
}
