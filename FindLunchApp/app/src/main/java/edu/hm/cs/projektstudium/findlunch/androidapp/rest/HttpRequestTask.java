package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;


/**
 * The type Http request task
 * performs a REST request
 * of an instance of {@link Request}
 * through {@link AsyncTask}
 * within a separate thread.
 */
public class HttpRequestTask extends AsyncTask<Request,String,Request> {

    /**
     * The Progress dialog
     * that shows the progress
     * of the request on the user interface.
     */
    private final ProgressDialog progressDialog;
    /**
     * The Context.
     */
    private final Context context;

    /**
     * Instantiates a new Http request task.
     *
     * @param context the context
     */
    public HttpRequestTask(Context context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Request doInBackground(Request... params) {
        // variable for the request
        Request request = null;

        if (params.length > 0 && params[0] != null) {
            request = params[0];
            publishProgress(request.getLoadingMessage());
            request.performRequest();
        }

        return request;
    }

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(context.getResources().getString(R.string.text_loading_data));
        progressDialog.setCancelable(false);
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param requestResponse The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Request requestResponse) {
        super.onPostExecute(requestResponse);
        requestResponse.sendRequestResponse();
        progressDialog.dismiss();
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        progressDialog.setMessage(values[0] + "\n" +context.getResources().getString(R.string.text_please_wait));
        progressDialog.show();
    }

    /**
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     * <p/>
     * <p>The default implementation simply invokes {@link #onCancelled()} and
     * ignores the result. If you write your own implementation, do not call
     * <code>super.onCancelled(result)</code>.</p>
     *
     * @param greeting The result, if any, computed in
     *                 {@link #doInBackground(Object[])}, can be null
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    @Override
    protected void onCancelled(Request greeting) {
        super.onCancelled(greeting);
    }

    /**
     * <p>Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.</p>
     * <p/>
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     *
     * @see #onCancelled(Object)
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
