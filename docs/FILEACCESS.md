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

