/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.digipost.android.authentication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

import android.content.Context;
import android.util.Log;

import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

public class KeyStoreAdapter {

	private final com.facebook.crypto.Crypto crypto;
	Entity entity = new Entity("refresh_token");

	public KeyStoreAdapter(final Context context) {
		crypto = new com.facebook.crypto.Crypto(
				  new SharedPrefsBackedKeyChain(context),
				  new SystemNativeCryptoLibrary());
	}

	public boolean isAvailable() {
		return crypto.isAvailable();
	}

	public String decrypt(final String ciphertext) {
		try {
			InputStream inputStream = crypto.getCipherInputStream(
			  new ByteArrayInputStream(Base64.decode(ciphertext.getBytes(), Base64.DEFAULT)),
			  entity);

			int read;
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((read = inputStream.read(buffer)) != -1) {
			  out.write(buffer, 0, read);
			}
			inputStream.close();
			out.close();
			return new String(out.toByteArray());
		} catch (Exception e) {
			Log.w("Crypto", e);
		}
		return null;
	}

	public String encrypt(final String plaintext) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStream outputStream = crypto.getCipherOutputStream(out, entity);
			outputStream.write(plaintext.getBytes());
			outputStream.close();
			return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CryptoInitializationException e) {
			e.printStackTrace();
		} catch (KeyChainException e) {
			e.printStackTrace();
		}
		return null;

	}
}