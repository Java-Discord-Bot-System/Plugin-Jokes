package src.com.almightyalpaca.discord.bot.plugin.joke;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.almightyalpaca.discord.bot.system.command.AbstractCommand;
import com.almightyalpaca.discord.bot.system.command.annotation.Command;
import com.almightyalpaca.discord.bot.system.events.CommandEvent;
import com.almightyalpaca.discord.bot.system.exception.PluginLoadingException;
import com.almightyalpaca.discord.bot.system.exception.PluginUnloadingException;
import com.almightyalpaca.discord.bot.system.plugins.Plugin;
import com.almightyalpaca.discord.bot.system.plugins.PluginInfo;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.MessageBuilder.Formatting;
import net.dv8tion.jda.entities.User;

public class JokePlugin extends Plugin {
	class JokeCommand extends AbstractCommand {

		public JokeCommand() {
			super("joke", "Tell a joke", "joke (name)");
		}

		@Command(dm = true, guild = true, async = true)
		public void onCommand(final CommandEvent event) {
			this.onCommand(event, (String) null);
		}

		@Command(dm = true, guild = true, priority = 1, async = true)
		public void onCommand(final CommandEvent event, final int id) {
			this.onCommand(event, id, null, null);
		}

		@Command(dm = true, guild = true, priority = 1, async = true)
		public void onCommand(final CommandEvent event, final int id, final String name) {
			if (name != null && name.contains(" ")) {
				final String[] names = name.split(" ", 2);
				this.onCommand(event, id, names[0], names[1]);
			} else {
				this.onCommand(event, id, "", name);
			}
		}

		@Command(dm = true, guild = true, priority = 1, async = true)
		public void onCommand(final CommandEvent event, final int id, final String firstName, final String lastName) {
			final MessageBuilder builder = new MessageBuilder();

			String key = "random";
			if (id != -1) {
				key = String.valueOf(id);
			}
			final HttpRequest request = Unirest.get("http://api.icndb.com/jokes/" + key).queryString("escape", "HTML");

			if (firstName != null) {
				request.queryString("firstName", firstName);
			}
			if (lastName != null) {
				request.queryString("lastName", lastName);
			}

			try {
				final JSONObject object = new JSONObject(request.asString().getBody());

//				if (object.getString("type") != "success") {
//					throw new IllegalArgumentException("Lookup failed: " + object.toString(4));
//				}

				String joke = object.getJSONObject("value").getString("joke");

				joke = StringEscapeUtils.unescapeHtml4(joke);
				joke = joke.trim();

				builder.appendString(joke);

			} catch (JSONException | UnirestException | IllegalArgumentException e) {
				e.printStackTrace();
				builder.appendString("An unexpected error occured!", Formatting.BOLD);
			}

			event.sendMessage(builder.build());
		}

		@Command(dm = true, guild = true, priority = 1, async = true)
		public void onCommand(final CommandEvent event, final int id, final User user) {
			this.onCommand(event, id, user.getUsername());
		}

		@Command(dm = true, guild = true, async = true)
		public void onCommand(final CommandEvent event, final String name) {
			this.onCommand(event, -1, name);
		}

		@Command(dm = true, guild = true, async = true)
		public void onCommand(final CommandEvent event, final String firstName, final String lastName) {
			this.onCommand(event, -1, firstName, lastName);
		}

		@Command(dm = true, guild = true, priority = 1, async = true)
		public void onCommand(final CommandEvent event, final User user) {
			this.onCommand(event, -1, user);
		}
	}

	private static final PluginInfo INFO = new PluginInfo("com.almightyalpaca.discord.bot.plugin.joke", "1.0.0", "Almighty Alpaca", "Joke Plugin", "Tells a joke using <http://www.icndb.com/>");

	public JokePlugin() {
		super(JokePlugin.INFO);
	}

	@Override
	public void load() throws PluginLoadingException {
		this.registerCommand(new JokeCommand());
	}

	@Override
	public void unload() throws PluginUnloadingException {

	}
}
