package jade.cheng;

/*
 * Copyright (c) 2011, Jade Cheng
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Hawaii nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Jade Cheng ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Jade Cheng BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * This program encodes command-line arguments as a Google search query,
 * downloads the results, and displays the corresponding links as output.
 */
public final class GoogleSearch {
	private String host = "google.ee";
	private String protocol="https";
	public GoogleSearch(){
		
	}
	public GoogleSearch setHost(String host){
		this.host = host;
        return this;
	}

    public GoogleSearch setProtocol(String protocol){
        this.protocol = protocol;
        return this;
    }
    /**
     * The main entry point of the program.
     *
     * @param args
     *            The command-line arguments. These arguments are encoded as a
     *            Google search query.
     * @throws IOException 
     */
    public void search(final String[] args) throws IOException {
        // Check for usage errors.
        if (args.length == 0) {
            System.out.println("usage: GoogleSearch query ...");
            return;
        }

            // Encode the command-line arguments as a Google search query.
            final URL url = encodeGoogleQuery(args);

            // Download the content from Google.
            System.out.println("Downloading [" + url + "]...\n");
            final String html = downloadString(url);

            // Parse and display the links.
            final List<URL> links = parseGoogleLinks(html);
            for (final URL link : links)
                System.out.println("  " + link);

    }

    /**
     * Reads all contents from an input stream and returns a string from the
     * data.
     *
     * @param stream
     *            The input stream to read.
     *
     * @return A string built from the contents of the input stream.
     *
     * @throws IOException
     *             Thrown if there is an error reading the stream.
     */
    private String downloadString(final InputStream stream)
            throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while (-1 != (ch = stream.read()))
            out.write(ch);
        return out.toString();
    }

    /**
     * Downloads the contents of a URL as a String. This method alters the
     * User-Agent of the HTTP request header so that Google does not return
     * Error 403 Forbidden.
     *
     * @param url
     *            The URL to download.
     *
     * @return The content downloaded from the URL as a string.
     *
     * @throws IOException
     *             Thrown if there is an error downloading the content.
     */
    public String downloadString(final URL url) throws IOException {
        final String agent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US)";
        final URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", agent);
        connection.setConnectTimeout(900000);
        final InputStream stream = connection.getInputStream();
        return downloadString(stream);
    }

    /**
     * Encodes a string of arguments as a URL for a Google search query.
     *
     * @param args
     *            The array of arguments to pass to Google's search engine.
     *
     * @return A URL for a Google search query based on the arguments.
     */
    public URL encodeGoogleQuery(final String[] args) {
        try {
            final StringBuilder localAddress = new StringBuilder();
            localAddress.append("/search?q=");

            for (int i = 0; i < args.length; i++) {
                final String encoding = URLEncoder.encode(args[i], "UTF-8");
                localAddress.append(encoding);
                if (i + 1 < args.length)
                    localAddress.append("+");
            }

            return new URL(this.protocol, this.host, localAddress.toString());

        } catch (final IOException e) {
            // Errors should not occur under normal circumstances.
            throw new RuntimeException(
                    "An error occurred while encoding the query arguments.");
        }
    }

    /**
     * Parses HTML output from a Google search and returns a list of
     * corresponding links for the query. The parsing algorithm is crude and may
     * not work if Google changes the output of their results. This method works
     * adequately as of February 28, 2011.
     *
     * @param html
     *            The HTML output from Google search results.
     *
     * @return A list of links for the query.
     *
     * @throws IOException
     *             Thrown if there is an error parsing the results from Google
     *             or if one of the links returned by Google is not a valid URL.
     */
    private List<URL> parseGoogleLinks(final String html)
            throws IOException {

        // These tokens are adequate for parsing the HTML from Google. First,
        // find a heading-3 element with an "r" class. Then find the next anchor
        // with the desired link. The last token indicates the end of the URL
        // for the link.
        final String token1 = "<h3 class=\"r\">";
        final String token2 = "<a href=\"";
        final String token3 = "\"";

        final List<URL> links = new ArrayList<URL>();
        try {
            // Loop until all links are found and parsed. Find each link by
            // finding the beginning and ending index of the tokens defined
            // above.
            int index = 0;
            while (-1 != (index = html.indexOf(token1, index))) {
                final int result = html.indexOf(token2, index);
                final int urlStart = result + token2.length();
                final int urlEnd = html.indexOf(token3, urlStart);
                final String urlText = html.substring(urlStart, urlEnd);
                final URL url = new URL(urlText);
                links.add(url);

                index = urlEnd + token3.length();
            }

            return links;

        } catch (final MalformedURLException e) {
            throw new IOException("Failed to parse Google links.");
        } catch (final IndexOutOfBoundsException e) {
            throw new IOException("Failed to parse Google links.");
        }
    }
}