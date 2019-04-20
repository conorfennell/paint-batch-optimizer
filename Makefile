upload-api:
	apibuilder upload paintbatch-com paint-batch-optimizer api/paint-batch-optimizer.json --update-config --version "${version}"

update-generated-src:
	apibuilder update