# File Access

## Overview

Most apps will need some form of "state" specific to their instance.  In non-sensitive situations,
this would generally be flat files, databases, or a combination of the two.  Medical apps have
sensitive data that may need to be locally encrypted, or even stored remotely.  To facilitate this,
a simple data storage api is provided, along with a few standard schemes, and extension points if
you need something custom.

## Special considerations

Standard file storage is what I would call "simple" from an error handling perspective.  If you run
into any problems, you either have a bug in your code, you're out of space, or your hardware is
malfunctioning.  In any of these cases, the app should quit, because you're basically done and there's
no general way to deal with this.

If we're encrypting data locally, the situation is more complex.  Either the user needs to be prompted
for a passphrase, a server needs to be contacted, or some combination of things.  So, the hardware
and app may be fine, but you can't yet access data.  As a result, the app lifecycle will be somewhat
more complicated than in other situations.

If data is being stored remotely, any individual call has the potential to fail, so exception handling
is going to be a critical issue.

## FileAccess

This interface handles file access.  It needs to deal with authentication as well as data reads and writes.
There are a set of standard implementations.  If you need something custom, see code and javadocs.

### ClearFileAccess

Unencrypted local file storage.  This is simple, but obviously an issue if you need encrypted data.

### AesFileAccess

256 AES local encryption.  User is prompted with a passcode on first access.  This is used to generate
encryption keys, etc.

## Implementation

ResearchStackApplication should return and implementation of FileAccess.  That defines the protocol used.

To see the proper call sequence, see PassCodeActivity.  File interactions need to be delayed until
initFileAccess is called on FileAccess, and the FileAccess implementation is successfully initialized.
