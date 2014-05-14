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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import no.digipost.android.R;
import no.digipost.android.api.ContentOperations;
import no.digipost.android.api.exception.DigipostApiException;
import no.digipost.android.api.exception.DigipostAuthenticationException;
import no.digipost.android.api.exception.DigipostClientException;
import no.digipost.android.constants.ApiConstants;
import no.digipost.android.constants.ApplicationConstants;
import no.digipost.android.documentstore.DocumentContentStore;
import no.digipost.android.gui.MainContentActivity;
import no.digipost.android.gui.adapters.ReceiptArrayAdapter;
import no.digipost.android.gui.content.HtmlAndReceiptActivity;
import no.digipost.android.model.Receipt;
import no.digipost.android.model.Receipts;
import no.digipost.android.utilities.DialogUtitities;

public class ReceiptFragment extends ContentFragment {

	public ReceiptFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		super.listAdapter = new ReceiptArrayAdapter(getActivity(), R.layout.content_list_item);
		super.listView.setAdapter(listAdapter);
		super.listView.setMultiChoiceModeListener(new ReceiptMultiChoiceModeListener());
		super.listView.setOnItemClickListener(new ReceiptListOnItemClickListener());

		updateAccountMeta();

		return view;
	}

	@Override
	public int getContent() {
		return ApplicationConstants.RECEIPTS;
	}

	private void checkStatusAndDisplayReceipts(Receipts receipts) {
        if(isAdded()) {
            ArrayList<Receipt> receipt = receipts.getReceipt();
            ReceiptFragment.super.listAdapter.replaceAll(receipt);

            int numberOfCards = Integer.parseInt(receipts.getNumberOfCards());
            int numberOfCardsReadyForVerification = Integer.parseInt(receipts.getNumberOfCardsReadyForVerification());
            int numberOfReceiptsHiddenUntilVerification = Integer.parseInt(receipts.getNumberOfReceiptsHiddenUntilVerification());

            if (receipt.size() == 0) {
                if (numberOfCards == 0) {
                    setListEmptyViewText(getString(R.string.emptyview_receipt_intro_title), getString(R.string.emptyview_receipt_intro_message));
                } else if (numberOfCardsReadyForVerification > 0) {
                    setListEmptyViewText(getString(R.string.emptyview_receipt_verification_title),
                            getString(R.string.emptyview_receipt_verification_message));
                } else {
                    setListEmptyViewText(getString(R.string.emptyview_receipt_registrated_title),
                            getString(R.string.emptyview_receipt_registrated_message));
                }
            } else {
                if (numberOfCards == 0) {

                    setTopText(getString(R.string.receipt_toptext_register_cards));
                } else if (numberOfReceiptsHiddenUntilVerification == 1) {

                    setTopText(getString(R.string.receipt_toptext_one_hidden_receipt));
                } else if (numberOfCardsReadyForVerification > 1) {

                    setTopText(getString(R.string.receipt_toptext_multiple_hidden_receipts_start) + numberOfReceiptsHiddenUntilVerification
                            + getString(R.string.receipt_toptext_multiple_hidden_receipts_end));
                }
            }
        }
	}

	public void updateAccountMeta() {
		GetReceiptsMetaTask task = new GetReceiptsMetaTask();
		task.execute();
	}

	private void openListItem(Receipt receipt) {
		GetReceiptContentTask task = new GetReceiptContentTask(receipt);
		task.execute();
	}

	private void openReceipt(String receiptContent) {
		Intent intent = new Intent(getActivity(), HtmlAndReceiptActivity.class);
		intent.putExtra(super.INTENT_CONTENT, getContent());
		intent.putExtra(ApiConstants.GET_RECEIPT, receiptContent);
		startActivityForResult(intent, MainContentActivity.INTENT_REQUESTCODE);
	}

	private class GetReceiptContentTask extends AsyncTask<Void, Void, String> {
		private Receipt receipt;
		private String errorMessage;
		private boolean invalidToken;

		public GetReceiptContentTask(Receipt receipt) {
			this.receipt = receipt;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!ReceiptFragment.super.progressDialogIsVisible)
				ReceiptFragment.super.showContentProgressDialog(this, context.getString(R.string.loading_content));

		}

		@Override
		protected String doInBackground(Void... voids) {
			try {
				return ContentOperations.getReceiptContentHTML(context, receipt);
			} catch (DigipostAuthenticationException e) {
				Log.e(getClass().getName(), e.getMessage(), e);
				errorMessage = e.getMessage();
				invalidToken = true;
				return null;
			} catch (DigipostApiException e) {
				Log.e(getClass().getName(), e.getMessage(), e);
				errorMessage = e.getMessage();
				return null;
			} catch (DigipostClientException e) {
				Log.e(getClass().getName(), e.getMessage(), e);
				errorMessage = e.getMessage();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ReceiptFragment.super.taskIsRunning = false;
			ReceiptFragment.super.hideProgressDialog();

			if (result != null) {
				DocumentContentStore.setContent(receipt);
				openReceipt(result);
			} else {
				if (invalidToken) {
					activityCommunicator.requestLogOut();
				}

				DialogUtitities.showToast(ReceiptFragment.this.getActivity(), errorMessage);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			ReceiptFragment.super.taskIsRunning = false;
			ReceiptFragment.super.hideProgressDialog();
		}
	}

	private class GetReceiptsMetaTask extends AsyncTask<Void, Void, Receipts> {
		private String errorMessage;
		private boolean invalidToken;

		public GetReceiptsMetaTask() {
			errorMessage = "";
			invalidToken = false;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			activityCommunicator.onStartRefreshContent();
		}

		@Override
		protected Receipts doInBackground(final Void... params) {
			try {
				return ContentOperations.getAccountContentMetaReceipt(context);
			} catch (DigipostApiException e) {
				errorMessage = e.getMessage();
				return null;
			} catch (DigipostClientException e) {
				errorMessage = e.getMessage();
				return null;
			} catch (DigipostAuthenticationException e) {
				errorMessage = e.getMessage();
				invalidToken = true;
				return null;
			}
		}

		@Override
		protected void onPostExecute(final Receipts receipts) {
			super.onPostExecute(receipts);
			if (receipts != null) {
				checkStatusAndDisplayReceipts(receipts);
			} else {
				DialogUtitities.showToast(ReceiptFragment.this.context, errorMessage);

				if (invalidToken) {
					activityCommunicator.requestLogOut();
				} else if (listAdapter.isEmpty()) {
					ReceiptFragment.super.setListEmptyViewNoNetwork(true);
				}
			}

            activityCommunicator.onUpdateAccountMeta();
			activityCommunicator.onEndRefreshContent();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			activityCommunicator.onEndRefreshContent();
		}
	}

	private class ReceiptListOnItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(final AdapterView<?> arg0, final View view, final int position, final long arg3) {
			openListItem((Receipt) ReceiptFragment.super.listAdapter.getItem(position));
		}
	}

	private void deleteReceipt(Receipt receipt) {
		ArrayList<Object> receipts = new ArrayList<Object>();
		receipts.add(receipt);

		ContentDeleteTask task = new ContentDeleteTask(receipts);
		task.execute();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == MainContentActivity.INTENT_REQUESTCODE) {

				String action = data.getStringExtra(ApiConstants.FRAGMENT_ACTIVITY_RESULT_ACTION);

				if (action.equals(ApiConstants.DELETE)) {
					deleteReceipt(DocumentContentStore.getDocumentReceipt());
				}
			}
		}
	}

	private class ReceiptMultiChoiceModeListener extends ContentMultiChoiceModeListener {

		@Override
		public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
			super.onCreateActionMode(actionMode, menu);

			MenuItem moveDocument = menu.findItem(R.id.main_context_menu_move);
			moveDocument.setVisible(false);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, android.view.MenuItem menuItem) {
			super.onActionItemClicked(actionMode, menuItem);

			switch (menuItem.getItemId()) {
			case R.id.main_context_menu_delete:
				ReceiptFragment.super.deleteContent();
				break;
			}

			return true;
		}
	}
}
