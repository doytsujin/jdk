/*
 * Copyright (c) 2008, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.nio.fs;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;

/**
 * Bsd implementation of FileSystemProvider
 */

class BsdFileSystemProvider extends UnixFileSystemProvider {
    public BsdFileSystemProvider() {
        super();
    }

    @Override
    BsdFileSystem newFileSystem() {
        return new BsdFileSystem(this);
    }

    @Override
    BsdFileStore getFileStore(UnixPath path) throws IOException {
        return new BsdFileStore(path);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends FileAttributeView> V getFileAttributeView(Path obj,
                                                                Class<V> type,
                                                                LinkOption... options)
    {
        if (type == BasicFileAttributeView.class ||
            type == PosixFileAttributeView.class ||
            type == UserDefinedFileAttributeView.class) {

            UnixPath file = UnixPath.toUnixPath(obj);
            boolean followLinks = Util.followLinks(options);

            if (type == BasicFileAttributeView.class)
                return (V) BsdFileAttributeViews.createBasicView(file,
                                                                 followLinks);
            else if (type == PosixFileAttributeView.class)
                return (V) BsdFileAttributeViews.createPosixView(file,
                                                                 followLinks);
            // user-defined is the only possibility
            return (V) new BsdUserDefinedFileAttributeView(file, followLinks);
        }

        return super.getFileAttributeView(obj, type, options);
    }

    @Override
    public DynamicFileAttributeView getFileAttributeView(Path obj,
                                                         String name,
                                                         LinkOption... options)
    {
        if (name.equals("basic") || name.equals("posix") ||
            name.equals("unix")  || name.equals("user")) {

            UnixPath file = UnixPath.toUnixPath(obj);
            boolean followLinks = Util.followLinks(options);

            if (name.equals("basic"))
                return BsdFileAttributeViews.createBasicView(file, followLinks);

            if (name.equals("posix"))
                return BsdFileAttributeViews.createPosixView(file, followLinks);

            if (name.equals("unix"))
                return BsdFileAttributeViews.createUnixView(file, followLinks);

            // user-defined is the only possibility
            return new BsdUserDefinedFileAttributeView(file, followLinks);
        }

        return super.getFileAttributeView(obj, name, options);
    }
}
