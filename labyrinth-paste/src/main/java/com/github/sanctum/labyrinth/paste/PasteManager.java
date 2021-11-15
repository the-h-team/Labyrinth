package com.github.sanctum.labyrinth.paste;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.paste.option.Context;
import com.github.sanctum.labyrinth.paste.option.Expiration;
import com.github.sanctum.labyrinth.paste.operative.PasteResponse;
import com.github.sanctum.labyrinth.paste.type.HasteOptions;
import com.github.sanctum.labyrinth.paste.type.PasteOptions;
import com.github.sanctum.labyrinth.paste.option.Visibility;
import com.github.sanctum.labyrinth.paste.type.Hastebin;
import com.github.sanctum.labyrinth.paste.type.Pastebin;
import com.github.sanctum.labyrinth.paste.type.PastebinUser;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object manager for paste bin and haste bin api.
 */
public interface PasteManager {

	@Note("This is an overridable provision!")
	static PasteManager getInstance() {
		// skip using an optional, fast stop if a provider is already cached.
		PasteManager manager = LabyrinthProvider.getInstance().getServicesManager().load(PasteManager.class);
		if (manager != null) {
			return manager;
		}
		PasteManager instance = new PasteManager() {
			@Override
			public @NotNull Hastebin newHaste() {
				return new Hastebin() {

					private final HasteOptions options;

					{
						this.options = HasteOptions.empty();
					}

					@Override
					public @NotNull String getApiKey() {
						return "NA";
					}

					@Override
					public @NotNull HasteOptions getOptions() {
						return options;
					}

					@Override
					public PasteResponse read(String id) {
						return new PasteResponse() {
							@Override
							public String get() {
								return getAll()[0];
							}

							@Override
							public String[] getAll() {
								String response = "Unable to receive proper response.";
								try {
									String requestURL = id.contains("http") ? id : "https://www.toptal.com/developers/hastebin/raw/" + id;
									URL url = new URL(requestURL);
									HttpURLConnection conn = (HttpURLConnection) url.openConnection();
									conn.setDoOutput(true);
									conn.setInstanceFollowRedirects(false);
									conn.setRequestMethod("GET");
									conn.setRequestProperty("User-Agent", "Hastebin Java Api");
									conn.setUseCaches(false);
									List<String> lines = Resources.readLines(conn.getURL(), Charsets.UTF_8);
									return lines.toArray(new String[0]);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								return response.split(" ");
							}

						};
					}

					@Override
					public PasteResponse write(String... info) {
						return () -> {
							String response = "Unable to receive proper response.";
							StringBuilder builder = new StringBuilder();
							for (String s : info) {
								builder.append(s).append("\n");
							}
							try {
								byte[] postData = builder.toString().getBytes(StandardCharsets.UTF_8);
								int postDataLength = postData.length;

								String requestURL = "https://www.toptal.com/developers/hastebin/documents";
								URL url = new URL(requestURL);
								HttpURLConnection conn = (HttpURLConnection) url.openConnection();
								conn.setDoOutput(true);
								conn.setInstanceFollowRedirects(false);
								conn.setRequestMethod("POST");
								conn.setRequestProperty("User-Agent", "Hastebin Java Api");
								conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
								conn.setUseCaches(false);
								DataOutputStream wr;
								try {
									wr = new DataOutputStream(conn.getOutputStream());
									wr.write(postData);
									BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
									response = reader.readLine();
								} catch (IOException e) {
									e.printStackTrace();
								}

								if (response.contains("\"key\"")) {
									response = response.substring(response.indexOf(":") + 2, response.length() - 2);

									String postURL = getOptions().isRaw() ? "https://www.toptal.com/developers/hastebin/raw/" : "https://www.toptal.com/developers/hastebin/";
									response = postURL + response;
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							return response;
						};
					}

					@Override
					public PasteResponse write(Collection<? extends CharSequence> info) {
						return () -> {
							String response = "Unable to receive proper response.";
							StringBuilder builder = new StringBuilder();
							for (CharSequence s : info) {
								builder.append("*").append(" ").append(s).append("\n");
							}
							try {
								byte[] postData = builder.toString().getBytes(StandardCharsets.UTF_8);
								int postDataLength = postData.length;

								String requestURL = "https://www.toptal.com/developers/hastebin/documents";
								URL url = new URL(requestURL);
								HttpURLConnection conn = (HttpURLConnection) url.openConnection();
								conn.setDoOutput(true);
								conn.setInstanceFollowRedirects(false);
								conn.setRequestMethod("POST");
								conn.setRequestProperty("User-Agent", "Hastebin Java Api");
								conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
								conn.setUseCaches(false);
								DataOutputStream wr;
								try {
									wr = new DataOutputStream(conn.getOutputStream());
									wr.write(postData);
									BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
									response = reader.readLine();
								} catch (IOException e) {
									e.printStackTrace();
								}

								if (response.contains("\"key\"")) {
									response = response.substring(response.indexOf(":") + 2, response.length() - 2);

									String postURL = getOptions().isRaw() ? "https://www.toptal.com/developers/hastebin/raw/" : "https://www.toptal.com/developers/hastebin/";
									response = postURL + response;
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							return response;
						};
					}
				};
			}

			@Override
			public @NotNull Pastebin newPaste(@NotNull String apiKey) {
				return new Pastebin() {

					private final PasteOptions options;

					{
						this.options = new PasteOptions() {

							private Context language = () -> "text";
							private Context folder;
							private Expiration expiration = Expiration.TEN_MINUTE;
							private Visibility visibility = Visibility.PUBLIC;

							@Override
							public @NotNull Context getLanguage() {
								return language;
							}

							@Override
							public @Nullable Context getFolder() {
								return folder;
							}

							@Override
							public @NotNull Expiration getExpiration() {
								return expiration;
							}

							@Override
							public @NotNull Visibility getVisibility() {
								return visibility;
							}

							@Override
							public void setFolder(@NotNull Context context) {
								this.folder = context;
							}

							@Override
							public void setLanguage(@NotNull Context context) {
								this.language = context;
							}

							@Override
							public void setExpiration(@NotNull Expiration expiration) {
								this.expiration = expiration;
							}

							@Override
							public void setVisibility(@NotNull Visibility visibility) {
								this.visibility = visibility;
							}
						};
					}

					@Override
					public PasteResponse read(String id) {
						String response = "Unable to receive proper response.";
						try {
							URL url = new URL("https://pastebin.com/raw/");
							URLConnection con = url.openConnection();
							HttpURLConnection http = (HttpURLConnection) con;
							http.setRequestMethod("POST");
							http.setDoOutput(true);
							http.setDoInput(true);

							Map<String, String> arguments = new HashMap<>();
							arguments.put("paste_key", id);

							StringJoiner sj = new StringJoiner("&");
							for (Map.Entry<String, String> entry : arguments.entrySet()) {
								sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
										+ URLEncoder.encode(entry.getValue(), "UTF-8"));
							}

							http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
							http.connect();
							OutputStream os = http.getOutputStream();
							os.write(sj.toString().getBytes(StandardCharsets.UTF_8));
							InputStream is = http.getInputStream();
							response = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

						} catch (IOException urlException) {
							urlException.printStackTrace();
						}
						String finalResponse = response;
						return () -> finalResponse;
					}

					@Override
					public @NotNull String getApiKey() {
						return apiKey;
					}

					@Override
					public @NotNull PasteOptions getOptions() {
						return options;
					}

					@Override
					public @Nullable PastebinUser login(String username, String password) {
						try {
							URL url = new URL("https://pastebin.com/api/api_login.php");
							URLConnection con = url.openConnection();
							HttpURLConnection http = (HttpURLConnection) con;
							http.setRequestMethod("POST");
							http.setDoOutput(true);
							http.setDoInput(true);

							Map<String, String> arguments = new HashMap<>();
							arguments.put("api_dev_key", getApiKey());
							arguments.put("api_user_name", username);
							arguments.put("api_user_password", password);

							StringJoiner sj = new StringJoiner("&");
							for (Map.Entry<String, String> entry : arguments.entrySet()) {
								sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
										+ URLEncoder.encode(entry.getValue(), "UTF-8"));
							}

							http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
							http.connect();
							OutputStream os = http.getOutputStream();
							os.write(sj.toString().getBytes(StandardCharsets.UTF_8));
							InputStream is = http.getInputStream();
							final String result = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
							if (result == null || result.isEmpty()) return null;
							return new PastebinUser() {

								private final String userId;
								private final PasteOptions options;

								{
									this.userId = result;
									this.options = new PasteOptions() {

										private Context language = () -> "text";
										private Context folder;
										private Expiration expiration = Expiration.NEVER;
										private Visibility visibility = Visibility.PUBLIC;

										@Override
										public @NotNull Context getLanguage() {
											return language;
										}

										@Override
										public @Nullable Context getFolder() {
											return folder;
										}

										@Override
										public @NotNull Expiration getExpiration() {
											return expiration;
										}

										@Override
										public @NotNull Visibility getVisibility() {
											return visibility;
										}

										@Override
										public void setFolder(@NotNull Context context) {
											this.folder = context;
										}

										@Override
										public void setLanguage(@NotNull Context context) {
											this.language = context;
										}

										@Override
										public void setExpiration(@NotNull Expiration expiration) {
											this.expiration = expiration;
										}

										@Override
										public void setVisibility(@NotNull Visibility visibility) {
											this.visibility = visibility;
										}
									};
								}

								@Override
								public @NotNull String getId() {
									return userId;
								}

								@Override
								public boolean remove(@NotNull String id) {
									try {
										URL url = new URL("https://pastebin.com/api/api_post.php");
										URLConnection con = url.openConnection();
										HttpURLConnection http = (HttpURLConnection) con;
										http.setRequestMethod("POST");
										http.setDoOutput(true);
										http.setDoInput(true);

										Map<String, String> arguments = new HashMap<>();
										arguments.put("api_dev_key", getApiKey());
										arguments.put("api_user_key", getId());
										arguments.put("api_option", "delete");
										arguments.put("api_paste_key", id);


										StringJoiner sj = new StringJoiner("&");
										for (Map.Entry<String, String> entry : arguments.entrySet()) {
											sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
													+ URLEncoder.encode(entry.getValue(), "UTF-8"));
										}

										byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

										http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
										http.connect();

										OutputStream os = http.getOutputStream();
										os.write(out);
										return true;
									} catch (IOException urlException) {
										urlException.printStackTrace();
										return false;
									}
								}

								@Override
								public @NotNull String getApiKey() {
									return apiKey;
								}

								@Override
								public @NotNull PasteOptions getOptions() {
									return options;
								}

								@Override
								public PasteResponse read(String id) {
									return () -> {
										String response = "Unable to receive proper response.";
										try {
											URL url = new URL("https://pastebin.com/api/api_post.php");
											URLConnection con = url.openConnection();
											HttpURLConnection http = (HttpURLConnection) con;
											http.setRequestMethod("POST");
											http.setDoOutput(true);
											http.setDoInput(true);

											Map<String, String> arguments = new HashMap<>();
											arguments.put("api_dev_key", getApiKey());
											arguments.put("api_user_key", getId());
											arguments.put("api_option", "show_paste");
											arguments.put("api_paste_key", id);


											StringJoiner sj = new StringJoiner("&");
											for (Map.Entry<String, String> entry : arguments.entrySet()) {
												sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
														+ URLEncoder.encode(entry.getValue(), "UTF-8"));
											}

											byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

											http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
											http.connect();

											OutputStream os = http.getOutputStream();
											os.write(out);
											InputStream is = http.getInputStream();
											return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
										} catch (IOException urlException) {
											urlException.printStackTrace();
										}
										return response;
									};
								}

								@Override
								public PasteResponse write(String... info) {
									return () -> {
										String response = "Unable to receive proper response.";
										StringBuilder builder = new StringBuilder();
										for (String s : info) {
											builder.append(s).append("\n");
										}
										try {
											URL url = new URL("https://pastebin.com/api/api_post.php");
											URLConnection con = url.openConnection();
											HttpURLConnection http = (HttpURLConnection) con;
											http.setRequestMethod("POST");
											http.setDoOutput(true);
											http.setDoInput(true);

											Map<String, String> arguments = new HashMap<>();
											arguments.put("api_dev_key", getApiKey());
											arguments.put("api_user_key", getId());
											arguments.put("api_option", "paste");
											arguments.put("api_paste_code", builder.toString());
											arguments.put("api_paste_private", getOptions().getVisibility().toString());
											arguments.put("api_paste_expire_date", getOptions().getExpiration().toString());
											arguments.put("api_paste_format", getOptions().getLanguage().get().toLowerCase(Locale.ROOT));
											if (getOptions().getFolder() != null) {
												arguments.put("api_folder_key", getOptions().getFolder().get());
											}

											StringJoiner sj = new StringJoiner("&");
											for (Map.Entry<String, String> entry : arguments.entrySet()) {
												sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
														+ URLEncoder.encode(entry.getValue(), "UTF-8"));
											}

											byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

											http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
											http.connect();

											OutputStream os = http.getOutputStream();
											os.write(out);
											InputStream is = http.getInputStream();
											return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
										} catch (IOException urlException) {
											urlException.printStackTrace();
										}
										return response;
									};
								}

								@Override
								public PasteResponse write(Collection<? extends CharSequence> info) {
									return () -> {
										String response = "Unable to receive proper response.";
										StringBuilder builder = new StringBuilder();
										for (CharSequence s : info) {
											builder.append(s).append("\n");
										}
										try {
											URL url = new URL("https://pastebin.com/api/api_post.php");
											URLConnection con = url.openConnection();
											HttpURLConnection http = (HttpURLConnection) con;
											http.setRequestMethod("POST");
											http.setDoOutput(true);
											http.setDoInput(true);

											Map<String, String> arguments = new HashMap<>();
											arguments.put("api_dev_key", getApiKey());
											arguments.put("api_user_key", getId());
											arguments.put("api_option", "paste");
											arguments.put("api_paste_code", builder.toString());
											arguments.put("api_paste_private", getOptions().getVisibility().toString());
											arguments.put("api_paste_expire_date", getOptions().getExpiration().toString());
											arguments.put("api_paste_format", getOptions().getLanguage().get().toLowerCase(Locale.ROOT));
											if (getOptions().getFolder() != null) {
												arguments.put("api_folder_key", getOptions().getFolder().get());
											}

											StringJoiner sj = new StringJoiner("&");
											for (Map.Entry<String, String> entry : arguments.entrySet()) {
												sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
														+ URLEncoder.encode(entry.getValue(), "UTF-8"));
											}

											byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

											http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
											http.connect();

											OutputStream os = http.getOutputStream();
											os.write(out);
											InputStream is = http.getInputStream();
											return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
										} catch (IOException urlException) {
											urlException.printStackTrace();
										}
										return response;
									};
								}
							};
						} catch (IOException urlException) {
							urlException.printStackTrace();
							return null;
						}
					}

					@Override
					public PasteResponse write(String... info) {
						return () -> {
							String response = "Unable to receive proper response.";
							StringBuilder builder = new StringBuilder();
							for (String s : info) {
								builder.append(s).append("\n");
							}
							try {
								URL url = new URL("https://pastebin.com/api/api_post.php");
								URLConnection con = url.openConnection();
								HttpURLConnection http = (HttpURLConnection) con;
								http.setRequestMethod("POST");
								http.setDoOutput(true);
								http.setDoInput(true);

								Map<String, String> arguments = new HashMap<>();
								arguments.put("api_dev_key", getApiKey());
								arguments.put("api_option", "paste");
								arguments.put("api_paste_code", builder.toString());
								arguments.put("api_paste_private", getOptions().getVisibility().toString());
								arguments.put("api_paste_expire_date", getOptions().getExpiration().toString());
								arguments.put("api_paste_format", getOptions().getLanguage().get().toLowerCase(Locale.ROOT));
								if (getOptions().getFolder() != null) {
									arguments.put("api_folder_key", getOptions().getFolder().get());
								}

								StringJoiner sj = new StringJoiner("&");
								for (Map.Entry<String, String> entry : arguments.entrySet()) {
									sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
											+ URLEncoder.encode(entry.getValue(), "UTF-8"));
								}

								byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

								http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
								http.connect();

								OutputStream os = http.getOutputStream();
								os.write(out);
								InputStream is = http.getInputStream();
								return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
							} catch (IOException urlException) {
								urlException.printStackTrace();
							}
							return response;
						};
					}

					@Override
					public PasteResponse write(Collection<? extends CharSequence> info) {
						return () -> {
							String response = "Unable to receive proper response.";
							StringBuilder builder = new StringBuilder();
							for (CharSequence s : info) {
								builder.append(s).append("\n");
							}
							try {
								URL url = new URL("https://pastebin.com/api/api_post.php");
								URLConnection con = url.openConnection();
								HttpURLConnection http = (HttpURLConnection) con;
								http.setRequestMethod("POST");
								http.setDoOutput(true);
								http.setDoInput(true);

								Map<String, String> arguments = new HashMap<>();
								arguments.put("api_dev_key", getApiKey());
								arguments.put("api_option", "paste");
								arguments.put("api_paste_code", builder.toString());
								arguments.put("api_paste_private", getOptions().getVisibility().toString());
								arguments.put("api_paste_expire_date", getOptions().getExpiration().toString());
								arguments.put("api_paste_format", getOptions().getLanguage().get().toLowerCase(Locale.ROOT));
								if (getOptions().getFolder() != null) {
									arguments.put("api_folder_key", getOptions().getFolder().get());
								}

								StringJoiner sj = new StringJoiner("&");
								for (Map.Entry<String, String> entry : arguments.entrySet()) {
									sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
											+ URLEncoder.encode(entry.getValue(), "UTF-8"));
								}

								byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

								http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
								http.connect();

								OutputStream os = http.getOutputStream();
								os.write(out);
								InputStream is = http.getInputStream();
								return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
							} catch (IOException urlException) {
								urlException.printStackTrace();
							}
							return response;
						};
					}
				};
			}
		};
		LabyrinthProvider.getInstance().getServicesManager().register(instance, LabyrinthProvider.getInstance().getPluginInstance(), ServicePriority.Low);
		return instance;
	}

	@NotNull Hastebin newHaste();

	@Note("This method requires a unique api key! Make sure you have an account registered")
	@NotNull Pastebin newPaste(@NotNull String apiKey);

}
