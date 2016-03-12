import json

with open('App/Represent/mobile/src/main/assets/election-county-2012.json', 'r') as f:
    results = json.load(f)

final = {}
for result in results:
	if result['state-postal'] not in final:
		final[result['state-postal']] = {}
	final[result['state-postal']][result['county-name']] = result["romney-percentage"]

with open('App/Represent/mobile/src/main/assets/election_results_2012.json', 'w') as f:
    json.dump(final, f)

print('done.')