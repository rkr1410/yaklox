package net.rkr1410.yaklox

// For exit codes, I’m using the conventions defined in the UNIX “sysexits.h” header.
// It’s the closest thing to a standard I could find.
/**
 * EX_USAGE (64) The command was used incorrectly, e.g., with the
 *               wrong number of arguments, a bad flag, a bad syntax
 *               in a parameter, or whatever.
 */
const val EX_USAGE = 64;

/**
 * EX_DATAERR (65) The input data was incorrect in some way.  This
 *                 should only be used for user's data and not system
 *                 files.
 */
const val EX_DATAERR = 65;

/**
 * EX_NOINPUT (66) An input file (not a system file) did not exist or
 *                 was not readable. This could also include errors
 *                 like ``No message'' to a mailer (if it cared to
 *                 catch it).
 */
const val EX_NOINPUT = 66;

/**
 *  EX_SOFTWARE (70) An internal software error has been detected.  This
 *                   should be limited to non-operating system related
 *                   errors as possible.
 */
const val EX_SOFTWARE = 70;
