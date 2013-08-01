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

package no.digipost.android.gui.fragments;

import no.digipost.android.R;
import no.digipost.android.constants.ApiConstants;
import no.digipost.android.constants.ApplicationConstants;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class WorkareaFragment extends DocumentFragment {
	public WorkareaFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

        super.listView.setMultiChoiceModeListener(new WorkareaMultiChoiceModeListener());

		return view;
	}

	@Override
	public int getContent() {
		return ApplicationConstants.WORKAREA;
	}

	private class WorkareaMultiChoiceModeListener extends DocumentMultiChoiceModeListener {

		@Override
		public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
			super.onCreateActionMode(actionMode, menu);

			MenuItem toWorkarea = menu.findItem(R.id.main_context_menu_workarea);
			toWorkarea.setVisible(false);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, android.view.MenuItem menuItem) {
			super.onActionItemClicked(actionMode, menuItem);

			switch (menuItem.getItemId()) {
			case R.id.main_context_menu_archive:
                moveDocument(ApiConstants.LOCATION_ARCHIVE);
				break;
			}

			return true;
		}
	}
}