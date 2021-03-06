/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.util;

import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.*;

/** Test class for URLUtil */
public class TestURLUtil {

  @Test
  public void testGetDomainName() throws Exception {

    URL url = null;

    url = new URL("http://nutch.apache.org");
    assertEquals("apache.org", URLUtil.getDomainName(url));

    url = new URL("http://en.wikipedia.org/wiki/Java_coffee");
    assertEquals("wikipedia.org", URLUtil.getDomainName(url));

    url = new URL("http://140.211.11.130/foundation/contributing.html");
    assertEquals("140.211.11.130", URLUtil.getDomainName(url));

    url = new URL("http://www.example.co.uk:8080/index.html");
    assertEquals("example.co.uk", URLUtil.getDomainName(url));

    url = new URL("http://com");
    assertEquals("com", URLUtil.getDomainName(url));

    url = new URL("http://www.example.co.uk.com");
    assertEquals("uk.com", URLUtil.getDomainName(url));

    // "nn" is not a tld
    url = new URL("http://example.com.nn");
    assertEquals("nn", URLUtil.getDomainName(url));

    url = new URL("http://");
    assertEquals("", URLUtil.getDomainName(url));

    url = new URL("http://www.edu.tr.xyz");
    assertEquals("xyz", URLUtil.getDomainName(url));

    url = new URL("http://www.example.c.se");
    assertEquals("example.c.se", URLUtil.getDomainName(url));

    // plc.co.im is listed as a domain suffix
    url = new URL("http://www.example.plc.co.im");
    assertEquals("example.plc.co.im", URLUtil.getDomainName(url));

    // 2000.hu is listed as a domain suffix
    url = new URL("http://www.example.2000.hu");
    assertEquals("example.2000.hu", URLUtil.getDomainName(url));

    // test non-ascii
    url = new URL("http://www.example.??????.tw");
    assertEquals("example.??????.tw", URLUtil.getDomainName(url));

  }

  @Test
  public void testGetDomainSuffix() throws Exception {
    URL url = null;

    url = new URL("http://lucene.apache.org/nutch");
    assertEquals("org", URLUtil.getDomainSuffix(url).getDomain());

    url = new URL("http://140.211.11.130/foundation/contributing.html");
    assertNull(URLUtil.getDomainSuffix(url));

    url = new URL("http://www.example.co.uk:8080/index.html");
    assertEquals("co.uk", URLUtil.getDomainSuffix(url).getDomain());

    url = new URL("http://com");
    assertEquals("com", URLUtil.getDomainSuffix(url).getDomain());

    url = new URL("http://www.example.co.uk.com");
    assertEquals("com", URLUtil.getDomainSuffix(url).getDomain());

    // "nn" is not a tld
    url = new URL("http://example.com.nn");
    assertNull(URLUtil.getDomainSuffix(url));

    url = new URL("http://");
    assertNull(URLUtil.getDomainSuffix(url));

    url = new URL("http://www.edu.tr.xyz");
    assertNull(URLUtil.getDomainSuffix(url));

    url = new URL("http://subdomain.example.edu.tr");
    assertEquals("edu.tr", URLUtil.getDomainSuffix(url).getDomain());

    url = new URL("http://subdomain.example.presse.fr");
    assertEquals("presse.fr", URLUtil.getDomainSuffix(url).getDomain());

    url = new URL("http://subdomain.example.presse.tr");
    assertEquals("tr", URLUtil.getDomainSuffix(url).getDomain());

    // plc.co.im is listed as a domain suffix
    url = new URL("http://www.example.plc.co.im");
    assertEquals("plc.co.im", URLUtil.getDomainSuffix(url).getDomain());

    // 2000.hu is listed as a domain suffix
    url = new URL("http://www.example.2000.hu");
    assertEquals("2000.hu", URLUtil.getDomainSuffix(url).getDomain());

    // test non-ascii
    url = new URL("http://www.example.??????.tw");
    assertEquals("??????.tw", URLUtil.getDomainSuffix(url).getDomain());

  }

  @Test
  public void testGetHostBatches() throws Exception {
    URL url;
    String[] batches;

    url = new URL("http://subdomain.example.edu.tr");
    batches = URLUtil.getHostBatches(url);
    assertEquals("subdomain", batches[0]);
    assertEquals("example", batches[1]);
    assertEquals("edu", batches[2]);
    assertEquals("tr", batches[3]);

    url = new URL("http://");
    batches = URLUtil.getHostBatches(url);
    assertEquals(1, batches.length);
    assertEquals("", batches[0]);

    url = new URL("http://140.211.11.130/foundation/contributing.html");
    batches = URLUtil.getHostBatches(url);
    assertEquals(1, batches.length);
    assertEquals("140.211.11.130", batches[0]);

    // test non-ascii
    url = new URL("http://www.example.??????.tw");
    batches = URLUtil.getHostBatches(url);
    assertEquals("www", batches[0]);
    assertEquals("example", batches[1]);
    assertEquals("??????", batches[2]);
    assertEquals("tw", batches[3]);

  }

  @Test
  public void testChooseRepr() throws Exception {

    String aDotCom = "http://www.a.com";
    String bDotCom = "http://www.b.com";
    String aSubDotCom = "http://www.news.a.com";
    String aQStr = "http://www.a.com?y=1";
    String aPath = "http://www.a.com/xyz/index.html";
    String aPath2 = "http://www.a.com/abc/page.html";
    String aPath3 = "http://www.news.a.com/abc/page.html";

    // 1) different domain them keep dest, temp or perm
    // a.com -> b.com*
    assertEquals(bDotCom, URLUtil.chooseRepr(aDotCom, bDotCom, true));
    assertEquals(bDotCom, URLUtil.chooseRepr(aDotCom, bDotCom, false));

    // 2) permanent and root, keep src
    // *a.com -> a.com?y=1 || *a.com -> a.com/xyz/index.html
    assertEquals(aDotCom, URLUtil.chooseRepr(aDotCom, aQStr, false));
    assertEquals(aDotCom, URLUtil.chooseRepr(aDotCom, aPath, false));

    // 3) permanent and not root and dest root, keep dest
    // a.com/xyz/index.html -> a.com*
    assertEquals(aDotCom, URLUtil.chooseRepr(aPath, aDotCom, false));

    // 4) permanent and neither root keep dest
    // a.com/xyz/index.html -> a.com/abc/page.html*
    assertEquals(aPath2, URLUtil.chooseRepr(aPath, aPath2, false));

    // 5) temp and root and dest not root keep src
    // *a.com -> a.com/xyz/index.html
    assertEquals(aDotCom, URLUtil.chooseRepr(aDotCom, aPath, true));

    // 6) temp and not root and dest root keep dest
    // a.com/xyz/index.html -> a.com*
    assertEquals(aDotCom, URLUtil.chooseRepr(aPath, aDotCom, true));

    // 7) temp and neither root, keep shortest, if hosts equal by path else by
    // hosts
    // a.com/xyz/index.html -> a.com/abc/page.html*
    // *www.a.com/xyz/index.html -> www.news.a.com/xyz/index.html
    assertEquals(aPath2, URLUtil.chooseRepr(aPath, aPath2, true));
    assertEquals(aPath, URLUtil.chooseRepr(aPath, aPath3, true));

    // 8) temp and both root keep shortest sub domain
    // *www.a.com -> www.news.a.com
    assertEquals(aDotCom, URLUtil.chooseRepr(aDotCom, aSubDotCom, true));
  }

  // from RFC3986 section 5.4.1
  private static String baseString = "http://a/b/c/d;p?q";
  private static String[][] targets = new String[][] {
      // unknown protocol {"g:h" , "g:h"},
      { "g", "http://a/b/c/g" }, { "./g", "http://a/b/c/g" },
      { "g/", "http://a/b/c/g/" }, { "/g", "http://a/g" },
      { "//g", "http://g" }, { "?y", "http://a/b/c/d;p?y" },
      { "g?y", "http://a/b/c/g?y" }, { "#s", "http://a/b/c/d;p?q#s" },
      { "g#s", "http://a/b/c/g#s" }, { "g?y#s", "http://a/b/c/g?y#s" },
      { ";x", "http://a/b/c/;x" }, { "g;x", "http://a/b/c/g;x" },
      { "g;x?y#s", "http://a/b/c/g;x?y#s" }, { "", "http://a/b/c/d;p?q" },
      { ".", "http://a/b/c/" }, { "./", "http://a/b/c/" },
      { "..", "http://a/b/" }, { "../", "http://a/b/" },
      { "../g", "http://a/b/g" }, { "../..", "http://a/" },
      { "../../", "http://a/" }, { "../../g", "http://a/g" } };

  @Test
  public void testResolveURL() throws Exception {
    // test NUTCH-436
    URL u436 = new URL("http://a/b/c/d;p?q#f");
    assertEquals("http://a/b/c/d;p?q#f", u436.toString());
    URL abs = URLUtil.resolveURL(u436, "?y");
    assertEquals("http://a/b/c/d;p?y", abs.toString());
    // test NUTCH-566
    URL u566 = new URL("http://www.fleurie.org/entreprise.asp");
    abs = URLUtil.resolveURL(u566, "?id_entrep=111");
    assertEquals("http://www.fleurie.org/entreprise.asp?id_entrep=111",
        abs.toString());
    URL base = new URL(baseString);
    assertEquals("base url parsing", baseString, base.toString());
    for (int i = 0; i < targets.length; i++) {
      URL u = URLUtil.resolveURL(base, targets[i][0]);
      assertEquals(targets[i][1], targets[i][1], u.toString());
    }
  }

  @Test
  public void testToUNICODE() throws Exception {
    assertEquals("http://www.??evir.com",
        URLUtil.toUNICODE("http://www.xn--evir-zoa.com"));
    assertEquals("http://uni-t??bingen.de/",
        URLUtil.toUNICODE("http://xn--uni-tbingen-xhb.de/"));
    assertEquals(
        "http://www.medizin.uni-t??bingen.de:8080/search.php?q=abc#p1",
        URLUtil
            .toUNICODE("http://www.medizin.xn--uni-tbingen-xhb.de:8080/search.php?q=abc#p1"));

  }

  @Test
  public void testToASCII() throws Exception {
    assertEquals("http://www.xn--evir-zoa.com",
        URLUtil.toASCII("http://www.??evir.com"));
    assertEquals("http://xn--uni-tbingen-xhb.de/",
        URLUtil.toASCII("http://uni-t??bingen.de/"));
    assertEquals(
        "http://www.medizin.xn--uni-tbingen-xhb.de:8080/search.php?q=abc#p1",
        URLUtil
            .toASCII("http://www.medizin.uni-t??bingen.de:8080/search.php?q=abc#p1"));
  }

  @Test
  public void testFileProtocol() throws Exception {
    // keep one single slash NUTCH-XXX
    assertEquals("file:/path/file.html",
        URLUtil.toASCII("file:/path/file.html"));
    assertEquals("file:/path/file.html",
        URLUtil.toUNICODE("file:/path/file.html"));
  }

}
