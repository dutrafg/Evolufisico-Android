#!/usr/bin/env python3
"""Create 50,000 PLANNED QA checkpoints per Beta 7–10, not finished features."""
import argparse
from pathlib import Path
SCREENS=("Início","Módulos","Clientes","Ficha do cliente","Avaliações","Formulários","Agenda","Cobranças","Evolução","Configurações")
AREAS=("layout","cores","tipografia","espaçamento","ícones","sombras","componentes","responsividade","acessibilidade","QA")
SCENARIOS=("inicial","com dados","vazio","erro","retrato","paisagem","tablet","telefone compacto","fonte ampliada","modo escuro")
def build(beta):
    if beta not in (7,8,9,10):
        raise ValueError("Versões válidas: 7 a 10")
    index=0
    for screen in SCREENS:
        for area in AREAS:
            for scenario in SCENARIOS:
                for check in range(1,51):
                    index+=1
                    yield f"- [ ] {index:05d} | Beta {beta} | {screen} | {area} | {scenario} | revisão {check:02d}: especificar, implementar, testar, evidenciar."
    assert index==50000
if __name__=="__main__":
    parser=argparse.ArgumentParser()
    parser.add_argument("beta",type=int,choices=(7,8,9,10))
    parser.add_argument("--output",type=Path,required=True)
    args=parser.parse_args()
    args.output.parent.mkdir(parents=True,exist_ok=True)
    with args.output.open("w",encoding="utf8") as file:
        file.write(f"# Beta {args.beta} — 50.000 checkpoints planejados (NÃO executados)\n\n")
        for item in build(args.beta): file.write(item+"\n")
    print("50.000 checkpoints gerados:",args.output)
