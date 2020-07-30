/*
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
package net.hasor.web;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * <p> This class represents a file or form item that was received within a
 * <code>multipart/form-data</code> POST request.
 *
 * @version $Id: FileItem.java 1454690 2013-03-09 12:08:48Z simonetripodi $
 * @since 1.3 additionally implements FileItemHeadersSupport
 */
public interface FileItem extends FileItemStream {
    /**
     * Returns the size of the file item.
     * @return The size of the file item, in bytes.
     */
    public long getSize();

    /**
     * A convenience method to write an uploaded item to disk. The client code
     * is not concerned with whether or not the item is stored in memory, or on
     * disk in a temporary location. They just want to write the uploaded item to a file.
     * <p>
     * This method is not guaranteed to succeed if called more than once for
     * the same item. This allows a particular implementation to use, for
     * example, file renaming, where possible, rather than copying all of the
     * underlying data, thus gaining a significant performance benefit.
     *
     * @param outStream The <code>OutputStream</code> into which the uploaded item should be stored.
     * @throws Exception if an error occurs.
     */
    public void writeTo(OutputStream outStream) throws IOException;

    /**
     * Deletes the underlying storage for a file item, including deleting any
     * associated temporary disk file. Although this storage will be deleted
     * automatically when the <code>FileItem</code> instance is garbage
     * collected, this method can be used to ensure that this is done at an
     * earlier time, thus preserving system resources.
     */
    public void deleteOrSkip();

    /**
     * Returns the contents of the file item as an array of bytes.
     * @return The contents of the file item as an array of bytes.
     */
    public byte[] get() throws IOException;

    /**
     * Returns the contents of the file item as a String, using the specified
     * encoding.  This method uses {@link #get()} to retrieve the
     * contents of the item.
     *
     * @param encoding The character encoding to use.
     * @return The contents of the item, as a string.
     * @throws UnsupportedEncodingException if the requested character encoding is not available.
     */
    public String getString(String encoding) throws IOException;

    /**
     * Returns the contents of the file item as a String, using the default
     * character encoding.  This method uses {@link #get()} to retrieve the
     * contents of the item.
     * @return The contents of the item, as a string.
     */
    public String getString() throws IOException;
}