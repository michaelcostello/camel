/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.printer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javax.print.DocFlavor;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;

import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;

public class PrinterConfiguration {
    private URI uri;
    private String hostname;
    private int port;
    private String printername;
    private String printerPrefix;
    private int copies = 1;
    private String flavor;
    private DocFlavor docFlavor;
    private String mimeType;
    private String mediaSize;
    private MediaSizeName mediaSizeName;
    private String sides;
    private Sides internalSides;
    private String orientation;
    private OrientationRequested internalOrientation;
    private boolean sendToPrinter = true;
    private String mediaTray;

    public PrinterConfiguration() {
    }

    public PrinterConfiguration(URI uri) throws URISyntaxException {
        this.uri = uri;
    }

    public void parseURI(URI uri) throws Exception {
        String protocol = uri.getScheme();

        if (!protocol.equalsIgnoreCase("lpr")) {
            throw new IllegalArgumentException("Unrecognized Print protocol: " + protocol + " for uri: " + uri);
        }

        setUri(uri);
        setHostname(uri.getHost());
        setPort(uri.getPort());

        // use path as printer name, but without any leading slashes
        String path = uri.getPath();
        path = ObjectHelper.removeStartingCharacters(path, '/');
        path = ObjectHelper.removeStartingCharacters(path, '\\');
        setPrintername(path);

        Map<String, Object> printSettings = URISupport.parseParameters(uri);
        setFlavor((String) printSettings.get("flavor"));
        setMimeType((String) printSettings.get("mimeType"));
        setDocFlavor(assignDocFlavor(flavor, mimeType));

        setPrinterPrefix((String) printSettings.get("printerPrefix"));

        if (printSettings.containsKey("copies")) {
            setCopies(Integer.valueOf((String) printSettings.get("copies")));
        }
        setMediaSize((String) printSettings.get("mediaSize"));
        setSides((String) printSettings.get("sides"));
        setOrientation((String) printSettings.get("orientation"));
        setMediaSizeName(assignMediaSize(mediaSize));
        setInternalSides(assignSides(sides));
        setInternalOrientation(assignOrientation(orientation));
        if (printSettings.containsKey("sendToPrinter")) {
            if (!(Boolean.valueOf((String) printSettings.get("sendToPrinter")))) {
                setSendToPrinter(false);
            }
        }

        if (printSettings.containsKey("mediaTray")) {
            setMediaTray((String) printSettings.get("mediaTray"));
        }
    }

    private DocFlavor assignDocFlavor(String flavor, String mimeType) throws Exception {
        // defaults
        if (mimeType == null) {
            mimeType = "AUTOSENSE";
        }
        if (flavor == null) {
            flavor = "DocFlavor.BYTE_ARRAY";
        }

        DocFlavor d = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        DocFlavorAssigner docFlavorAssigner = new DocFlavorAssigner();
        if (mimeType.equalsIgnoreCase("AUTOSENSE")) {
            d = docFlavorAssigner.forMimeTypeAUTOSENSE(flavor);
        } else if (mimeType.equalsIgnoreCase("GIF")) {
            d = docFlavorAssigner.forMimeTypeGIF(flavor);
        } else if (mimeType.equalsIgnoreCase("JPEG")) {
            d = docFlavorAssigner.forMimeTypeJPEG(flavor);
        } else if (mimeType.equalsIgnoreCase("PDF")) {
            d = docFlavorAssigner.forMimeTypePDF(flavor);
        } else if (mimeType.equalsIgnoreCase("PCL")) {
            d = docFlavorAssigner.forMimeTypePCL(flavor);
        } else if (mimeType.equalsIgnoreCase("POSTSCRIPT")) {
            d = docFlavorAssigner.forMimeTypePOSTSCRIPT(flavor);
        } else if (mimeType.equalsIgnoreCase("TEXT_HTML_HOST")) {
            d = docFlavorAssigner.forMimeTypeHOST(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_HTML_US_ASCII")) {
            d = docFlavorAssigner.forMimeTypeUSASCII(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_HTML_UTF_16")) {
            d = docFlavorAssigner.forMimeTypeUTF16(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_HTML_UTF_16LE")) {
            d = docFlavorAssigner.forMimeTypeUTF16LE(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_HTML_UTF_16BE")) {
            d = docFlavorAssigner.forMimeTypeUTF16BE(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_HTML_UTF_8")) {
            d = docFlavorAssigner.forMimeTypeUTF8(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_PLAIN_HOST")) {
            d = docFlavorAssigner.forMimeTypeHOST(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_PLAIN_US_ASCII")) {
            d = docFlavorAssigner.forMimeTypeUSASCII(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_PLAIN_UTF_16")) {
            d = docFlavorAssigner.forMimeTypeUTF16(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_PLAIN_UTF_16LE")) {
            d = docFlavorAssigner.forMimeTypeUTF16LE(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_PLAIN_UTF_16BE")) {
            d = docFlavorAssigner.forMimeTypeUTF16BE(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_PLAIN_UTF_8")) {
            d = docFlavorAssigner.forMimeTypeUTF8(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_HTML")) {
            d = docFlavorAssigner.forMimeTypeBasic(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("TEXT_PLAIN")) {
            d = docFlavorAssigner.forMimeTypeBasic(flavor, mimeType);
        } else if (mimeType.equalsIgnoreCase("PAGEABLE")) {
            d = docFlavorAssigner.forMimeTypePAGEABLE(flavor);
        } else if (mimeType.equalsIgnoreCase("PRINTABLE")) {
            d = docFlavorAssigner.forMimeTypePRINTABLE(flavor);
        } else if (mimeType.equalsIgnoreCase("RENDERABLE_IMAGE")) {
            d = docFlavorAssigner.forMimeTypeRENDERABLEIMAGE(flavor);
        }

        return d;
    }

    private MediaSizeName assignMediaSize(String size) {
        MediaSizeAssigner mediaSizeAssigner = new MediaSizeAssigner();

        MediaSizeName answer;

        if (size == null) {
            // default to NA letter if no size configured
            answer = MediaSizeName.NA_LETTER;
        } else if (size.toLowerCase().startsWith("iso")) {
            answer = mediaSizeAssigner.selectMediaSizeNameISO(size);
        } else if (size.startsWith("jis")) {
            answer = mediaSizeAssigner.selectMediaSizeNameJIS(size);
        } else if (size.startsWith("na")) {
            answer = mediaSizeAssigner.selectMediaSizeNameNA(size);
        } else {
            answer = mediaSizeAssigner.selectMediaSizeNameOther(size);
        }

        return answer;
    }

    public Sides assignSides(String sidesString) {
        Sides answer;

        if (sidesString == null) {
            // default to one side if no slides configured
            answer = Sides.ONE_SIDED;
        } else if (sidesString.equalsIgnoreCase("one-sided")) {
            answer = Sides.ONE_SIDED;
        } else if (sidesString.equalsIgnoreCase("duplex")) {
            answer = Sides.DUPLEX;
        } else if (sidesString.equalsIgnoreCase("tumble")) {
            answer = Sides.TUMBLE;
        } else if (sidesString.equalsIgnoreCase("two-sided-short-edge")) {
            answer = Sides.TWO_SIDED_SHORT_EDGE;
        } else if (sidesString.equalsIgnoreCase("two-sided-long-edge")) {
            answer = Sides.TWO_SIDED_LONG_EDGE;
        } else {
            answer = Sides.ONE_SIDED;
        }

        return answer;
    }

    public OrientationRequested assignOrientation(final String orientation) {
        OrientationRequested answer;

        if (orientation == null) {
            // default to portrait
            answer = OrientationRequested.PORTRAIT;
        } else if (orientation.equalsIgnoreCase("portrait")) {
            answer = OrientationRequested.PORTRAIT;
        } else if (orientation.equalsIgnoreCase("landscape")) {
            answer = OrientationRequested.LANDSCAPE;
        } else if (orientation.equalsIgnoreCase("reverse-portrait")) {
            answer = OrientationRequested.REVERSE_PORTRAIT;
        } else if (orientation.equalsIgnoreCase("reverse-landscape")) {
            answer = OrientationRequested.REVERSE_LANDSCAPE;
        } else {
            answer = OrientationRequested.PORTRAIT;
        }

        return answer;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPrintername() {
        return printername;
    }

    public void setPrintername(String printername) {
        this.printername = printername;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public DocFlavor getDocFlavor() {
        return docFlavor;
    }

    public void setDocFlavor(DocFlavor docFlavor) {
        this.docFlavor = docFlavor;
    }

    public String getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(String mediaSize) {
        this.mediaSize = mediaSize;
    }

    public String getSides() {
        return sides;
    }

    public void setSides(String sides) {
        this.sides = sides;
    }

    public MediaSizeName getMediaSizeName() {
        return mediaSizeName;
    }

    public void setMediaSizeName(MediaSizeName mediaSizeName) {
        this.mediaSizeName = mediaSizeName;
    }

    public Sides getInternalSides() {
        return internalSides;
    }

    public void setInternalSides(Sides internalSides) {
        this.internalSides = internalSides;
    }

    public OrientationRequested getInternalOrientation() {
        return internalOrientation;
    }

    public void setInternalOrientation(OrientationRequested internalOrientation) {
        this.internalOrientation = internalOrientation;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isSendToPrinter() {
        return sendToPrinter;
    }

    public void setSendToPrinter(boolean sendToPrinter) {
        this.sendToPrinter = sendToPrinter;
    }

    public String getMediaTray() {
        return mediaTray;
    }

    public void setMediaTray(String mediaTray) {
        this.mediaTray = mediaTray;
    }

    public String getPrinterPrefix() {
        return printerPrefix;
    }

    public void setPrinterPrefix(String printerPrefix) {
        this.printerPrefix = printerPrefix;
    }
}
