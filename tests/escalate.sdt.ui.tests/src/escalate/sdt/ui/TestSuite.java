package escalate.sdt.ui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import escalate.sdt.ui.internal.actions.ToggleNatureActionSpec;
import escalate.sdt.ui.internal.wizards.NewScalateProjectWizardSpec;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ToggleNatureActionSpec.class, NewScalateProjectWizardSpec.class })
class TestSuite {
}
