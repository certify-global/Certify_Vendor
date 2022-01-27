package com.netronix.tools;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public class HexDump {
	private static final int		ROW_BYTES = 16;
	private static final int		ROW_QTR1 = 3;
	private static final int		ROW_HALF = 7;
	private static final int		ROW_QTR2 = 11;

	public static void
	dumpHexFile( String FilePath) {
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile( FilePath,"r");
			byte byReadPattern[] = new byte[(int) raf.length()];
			raf.read( byReadPattern);
			raf.close();
			HexDump.dumpHexData( System.out, "Test HexDump", byReadPattern, byReadPattern.length );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e( "dumpHexFile", "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e( "dumpHexFile", "IOException");
		}
	}
	
	public static void
	dumpHexData(String title, byte[] buf, int numBytes ) {
		if ( buf == null) return;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		dumpHexData( ps, title, buf, numBytes);
		String content = baos.toString(); // e.g. ISO-8859-1
		Log.d( title, content);
	}

	public static void
	dumpHexDataNoTextOneline (String title, byte[] buf, int numBytes ) {
		if ( buf == null) return;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		dumpHexData( ps, title, buf, numBytes, true, true);
		String content = baos.toString(); // e.g. ISO-8859-1
		Log.d( title, content);
	}

	public static void
	dumpHexData(PrintStream out, String title, byte[] buf, int numBytes) {
		dumpHexData (out, title, buf, numBytes, false, false);
	}
	
	
	public static void
	dumpHexData(PrintStream out, String title, byte[] buf, int numBytes, boolean hasNoText, boolean oneline )
		{
		int			rows, residue, i, j;
		byte[]		save_buf= new byte[ ROW_BYTES+2 ];
		byte[]		hex_buf = new byte[ 4 ];
		byte[]		idx_buf = new byte[ 8 ];
		byte[]		hex_chars = new byte[20];
		
		hex_chars[0] = '0';
		hex_chars[1] = '1';
		hex_chars[2] = '2';
		hex_chars[3] = '3';
		hex_chars[4] = '4';
		hex_chars[5] = '5';
		hex_chars[6] = '6';
		hex_chars[7] = '7';
		hex_chars[8] = '8';
		hex_chars[9] = '9';
		hex_chars[10] = 'A';
		hex_chars[11] = 'B';
		hex_chars[12] = 'C';
		hex_chars[13] = 'D';
		hex_chars[14] = 'E';
		hex_chars[15] = 'F';
		
		out.println( title + " - " + numBytes + " bytes." );
		rows = numBytes >> 4;
		residue = numBytes & 0x0000000F;
		for ( i = 0 ; i < rows ; i++ )
			{
			int hexVal = (i * ROW_BYTES);
			idx_buf[0] = hex_chars[((hexVal >> 12) & 15)];
			idx_buf[1] = hex_chars[((hexVal >> 8) & 15)];
			idx_buf[2] = hex_chars[((hexVal >> 4) & 15)];
			idx_buf[3] = hex_chars[(hexVal & 15)];

			if (oneline==false) 
				{
				String idxStr = new String( idx_buf, 0, 4 );
				out.print( idxStr + ": " );
				}
		
			for ( j = 0 ; j < ROW_BYTES ; j++ )
				{
				save_buf[j] = buf[(i * ROW_BYTES) + j];

				hex_buf[0] = hex_chars[(save_buf[j] >> 4) & 0x0F];
				hex_buf[1] = hex_chars[save_buf[j] & 0x0F];

				out.print( (char)hex_buf[0] );
				out.print( (char)hex_buf[1] );
				out.print( ' ' );

				if ( j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2 )
					out.print( " " );

				if (save_buf[j] < 0x20 || save_buf[j] > 0xD9)
					save_buf[j] = '.';
				}

			if (hasNoText==false) 
				{
				String saveStr = new String( save_buf, 0, j );
				out.println( " | " + saveStr + " |" );
				}
			else if (oneline==false)
				{
				out.println();
				}
			}
		
		if ( residue > 0 )
			{
			int hexVal = (i * ROW_BYTES);
			idx_buf[0] = hex_chars[((hexVal >> 12) & 15)];
			idx_buf[1] = hex_chars[((hexVal >> 8) & 15)];
			idx_buf[2] = hex_chars[((hexVal >> 4) & 15)];
			idx_buf[3] = hex_chars[(hexVal & 15)];

			if (oneline==false)
				{
				String idxStr = new String( idx_buf, 0, 4 );
				out.print( idxStr + ": " );
				}

			for ( j = 0 ; j < residue ; j++ )
				{
				save_buf[j] = buf[(i * ROW_BYTES) + j];

				hex_buf[0] = hex_chars[(save_buf[j] >> 4) & 0x0F];
				hex_buf[1] = hex_chars[save_buf[j] & 0x0F];

				out.print( (char)hex_buf[0] );
				out.print( (char)hex_buf[1] );
				out.print( ' ' );

				if ( j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2 )
					out.print( " " );

				if (save_buf[j] < 0x20 || save_buf[j] > 0xD9)
					save_buf[j] = '.';
				}
				
			for ( /*j INHERITED*/ ; j < ROW_BYTES ; j++)
				{
				save_buf[j] = ' ';
				out.print( "   " );
				if ( j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2 )
					out.print( " " );
				}
			
			if (hasNoText==false) 
				{
				String saveStr = new String( save_buf, 0, j );
				out.println( " | " + saveStr + " |" );
				}
			}
		}

	public void
	Hextdump_example( String[] args )
		{
		byte[] data = new byte[132];
		for ( int i = 0 ; i < 132 ; ++i ) data[i] = (byte)i;

		HexDump.dumpHexData( System.err, "Test HexDump", data, 132 );
		}

	}