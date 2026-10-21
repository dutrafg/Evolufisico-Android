import importlib.util
from pathlib import Path
p=Path(__file__).resolve().parents[1]/"scripts"/"generate_beta_roadmap.py"
s=importlib.util.spec_from_file_location("roadmap",p)
module=importlib.util.module_from_spec(s);s.loader.exec_module(module)
for b in (7,8,9,10):
    lines=list(module.build(b))
    assert len(lines)==50000
    assert lines[0].startswith("- [ ] 00001")
    assert lines[-1].startswith("- [ ] 50000")
    assert len(set(lines))==50000
print("200.000 verificações numeradas, 50.000 por Beta.")
