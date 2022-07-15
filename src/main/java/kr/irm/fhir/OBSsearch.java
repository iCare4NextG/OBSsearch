package kr.irm.fhir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import kr.irm.fhir.util.MyResponseHandler;
import kr.irm.fhir.util.URIBuilder;
import org.apache.commons.cli.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OBSsearch extends UtilContext {
	private static final Logger LOG = LoggerFactory.getLogger(OBSsearch.class);

	public static void main(String[] args) {
		LOG.info("starting mhdsend...");
		LOG.info("option args:{} ", Arrays.toString(args));
		Options opts = new Options();
		Map<String, Object> optionMap = new HashMap<String, Object>();
		setOptions(opts);

		// parse options
		if (parseOptions(optionMap, opts, args)) {
			LOG.error("mhdsearch failed: invalid options");
			System.exit(1);
		}

		doSearch(optionMap);
	}

	private static void setOptions(Options opts) {
		// help
		opts.addOption("h", "help", false, "help");

		// Commons
		opts.addOption("o", OPTION_OAUTH_TOKEN, true, "OAuth Token");
		opts.addOption("s", OPTION_SERVER_URL, true, "FHIR Server Base URL");
		opts.addOption("pu", OPTION_PATIENT_UUID, true, "Patient.id (UUID)");
		opts.addOption("ou", OPTION_OBSERVATION_UUID, true, "Observation.id (UUID)");

		opts.addOption("i", OPTION_ID, true, "id");
		opts.addOption("st", OPTION_STATUS, true, "status");
		opts.addOption("d", OPTION_DATE, true, "issued date");
		opts.addOption("c", OPTION_CATEGORY, true, "category");
		opts.addOption("cd", OPTION_CODE, true, "code");
		opts.addOption("sr", OPTION_SORT, true, "sort");
		opts.addOption("of", OPTION_OFFSET, true, "offset");
		opts.addOption("co", OPTION_COUNT, true, "count");
		opts.addOption("f", OPTION_FORMAT, true, "Response Format (application/fhir+json or application/fhir+xml)");
	}

	private static boolean parseOptions(Map<String, Object> optionMap, Options opts, String[] args) {
		boolean error = false;
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cl = parser.parse(opts, args);

			// HELP
			if (cl.hasOption("h") || args.length == 0) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(
						"OBSsearch [options]",
						"\nSearch Observation from Data Platform", opts,
						"Examples: $ ./OBSsearch --patient-id ...");
				System.exit(2);
			}

			// OAuth token (Required)
			if (cl.hasOption(OPTION_OAUTH_TOKEN)) {
				String oauth_token = cl.getOptionValue(OPTION_OAUTH_TOKEN);
				LOG.info("option {}={}", OPTION_OAUTH_TOKEN, oauth_token);

				optionMap.put(OPTION_OAUTH_TOKEN, oauth_token);
			}

			// FHIR
			// Server-url (Required)
			if (cl.hasOption(OPTION_SERVER_URL)) {
				String server_url = cl.getOptionValue(OPTION_SERVER_URL);
				LOG.info("option {}={}", OPTION_SERVER_URL, server_url);

				optionMap.put(OPTION_SERVER_URL, server_url);
			} else {
				error = true;
				LOG.error("option required: {}", OPTION_SERVER_URL);
			}

			// id
			if (cl.hasOption(OPTION_ID)) {
				String id = cl.getOptionValue(OPTION_ID);
				LOG.info("option {}={}", OPTION_ID, id);

				optionMap.put(OPTION_ID, id);
			}

			// Observation UUID
			if (cl.hasOption(OPTION_OBSERVATION_UUID)) {
				String observationUuid = cl.getOptionValue(OPTION_OBSERVATION_UUID);
				LOG.info("option {}={}", OPTION_OBSERVATION_UUID, observationUuid);

				optionMap.put(OPTION_OBSERVATION_UUID, observationUuid);
			}

			// Patient UUID
			if (cl.hasOption(OPTION_PATIENT_UUID)) {
				String patientUuid = cl.getOptionValue(OPTION_PATIENT_UUID);
				LOG.info("option {}={}", OPTION_PATIENT_UUID, patientUuid);

				optionMap.put(OPTION_PATIENT_UUID, patientUuid);
			}

			// status
			if (cl.hasOption(OPTION_STATUS)) {
				String[] component = cl.getOptionValue(OPTION_STATUS).split(",");
				List<String> statusList = getComponentList(component);
				LOG.info("option {}={}", OPTION_STATUS, statusList);

				optionMap.put(OPTION_STATUS, statusList);
			}

			// issuedDate
			if (cl.hasOption(OPTION_DATE)) {
				String[] component = cl.getOptionValue(OPTION_DATE).split(",");
				List<String> dateList = getComponentList(component);
				LOG.info("option {}={}", OPTION_DATE, dateList);

				optionMap.put(OPTION_DATE, dateList);
			}

			// category
			if (cl.hasOption(OPTION_CATEGORY)) {
				String[] component = cl.getOptionValue(OPTION_CATEGORY).split(",");
				List<String> categoryList = getComponentList(component);
				LOG.info("option {}={}", OPTION_CATEGORY, categoryList);

				optionMap.put(OPTION_CATEGORY, categoryList);
			}

			// code
			if (cl.hasOption(OPTION_CODE)) {
				String[] component = cl.getOptionValue(OPTION_CODE).split(",");
				List<String> codeList = getComponentList(component);
				LOG.info("option {}={}", OPTION_CODE, codeList);

				optionMap.put(OPTION_CODE, codeList);
			}

			// sort
			if (cl.hasOption(OPTION_SORT)) {
				String sort = cl.getOptionValue(OPTION_SORT);
				LOG.info("option {}={}", OPTION_SORT, sort);

				optionMap.put(OPTION_SORT, sort);
			}

			// offset
			if (cl.hasOption(OPTION_OFFSET)) {
				String offset = cl.getOptionValue(OPTION_OFFSET);
				LOG.info("option {}={}", OPTION_OFFSET, offset);

				optionMap.put(OPTION_OFFSET, offset);
			}

			// count
			if (cl.hasOption(OPTION_COUNT)) {
				String count = cl.getOptionValue(OPTION_COUNT);
				LOG.info("option {}={}", OPTION_COUNT, count);

				optionMap.put(OPTION_COUNT, count);
			}

			// format
			if (cl.hasOption(OPTION_FORMAT)) {
				String format = cl.getOptionValue(OPTION_FORMAT);
				LOG.info("option {}={}", OPTION_FORMAT, format);

				optionMap.put(OPTION_FORMAT, format);
			} else {
				String format = "application/fhir+json";
				optionMap.put(OPTION_FORMAT, format);
				LOG.info("option {}={}", OPTION_FORMAT, format);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return error;
	}

	private static String doSearch(Map<String, Object> optionMap) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String httpResult = "";
		try {
			URIBuilder uriBuilder = new URIBuilder((String) optionMap.get(OPTION_SERVER_URL));
			uriBuilder.addPath(OBSERVATION);

			if (optionMap.containsKey(OPTION_OBSERVATION_UUID)) {
				uriBuilder.addPath((String) optionMap.get(OPTION_OBSERVATION_UUID));
				uriBuilder.addParameter("_format", (String) optionMap.get(OPTION_FORMAT));
			} else {
				for (String key : optionMap.keySet()) {
					if (key != OPTION_OAUTH_TOKEN && key != OPTION_SERVER_URL && key != OPTION_OBSERVATION_UUID) {
						if (optionMap.get(key) instanceof String) {
							uriBuilder.addParameter(key, (String) optionMap.get(key));
						} else if (optionMap.get(key) instanceof List) {
							for (String s : (List<String>) optionMap.get(key)) {
								uriBuilder.addParameter(key, s);
							}
						}
					}
				}
			}

			String searchUrl = uriBuilder.build().toString();
			LOG.info("search url : {}", searchUrl);

			HttpGet httpGet = new HttpGet(searchUrl);
			httpGet.setHeader("Authorization", "Bearer " + optionMap.get(OPTION_OAUTH_TOKEN));

			ResponseHandler<String> responseHandler = new MyResponseHandler();
			httpResult = httpClient.execute(httpGet, responseHandler);
			LOG.info("Response : \n{}", httpResult);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpClient != null) httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return httpResult;
	}

	private static List<String> getComponentList(String[] component) {
		List<String> componentList = new ArrayList<>();
		for (String s : component) {
			componentList.add(s);
		}

		return componentList;
	}
}
