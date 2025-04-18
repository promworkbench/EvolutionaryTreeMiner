package org.processmining.plugins.etm.ui.plugins;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.etm.ETM;
import org.processmining.plugins.etm.experiments.StandardLogs;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.NAryTreeToProcessTree;
import org.processmining.plugins.etm.model.narytree.conversion.ProcessTreeToNAryTree;
import org.processmining.plugins.etm.parameters.ETMParam;
import org.processmining.plugins.etm.parameters.ETMParamFactory;
import org.processmining.processtree.ProcessTree;

@Plugin(
		name = "Mine a Process Tree with ETMr using parameters and classifier",
			parameterLabels = { "Process Tree", "Event log", "Classifier", "Parameters"},
			returnLabels = { "Process Tree" },
			returnTypes = { ProcessTree.class },
			userAccessible = true,
			help = "Repair a Process Tree with ETMr using parameters and classifier",
			handlesCancel = true,
			categories = {PluginCategory.Enhancement},
			keywords = {"ETM", "ETMr", "Process Tree", "Evolutionary", "Evolutionary Tree Miner", "Genetic", "Genetic Miner"})
public class ETMrwithoutGUI {

	@PluginVariant(
			variantLabel = "Mine a Process Tree with ETMr using default parameters and provided classifier",
				requiredParameterLabels = { 0, 1 })
	public static ProcessTree minePTWithClassifier(final PluginContext context, ProcessTree processTree, XLog eventlog,
			XEventClassifier classifier) {
		ETMParam param = ETMParamFactory.buildStandardParam(eventlog, context);

		
		return minePTWithParameters(context, processTree, eventlog, classifier, param);
	}

	@PluginVariant(
			variantLabel = "Mine a Process Tree with ETMr using provided parameters and provided classifier",
				requiredParameterLabels = { 0, 1, 2 })
	public static ProcessTree minePTWithParameters(final PluginContext context, ProcessTree processTree, XLog eventlog, XEventClassifier classifier, ETMParam param) {

		//TODO check if this is enough, should be..
		param.getCentralRegistry().updateEventClassifier(classifier);
		
		NAryTree[] seed = new NAryTree[1];
		ProcessTreeToNAryTree convertor = new ProcessTreeToNAryTree(param.getCentralRegistry().getEventClasses());
		seed[0] = convertor.convert(processTree);
		
		param.setSeed(seed);		
		
		ETM etm = new ETM(param);
		etm.run();

		NAryTree nat = etm.getResult();

		ProcessTree pt = NAryTreeToProcessTree.convert(nat, param.getCentralRegistry().getEventClasses());

		return pt;
	}

}
