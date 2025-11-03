#!/usr/bin/env python3
"""
JUnit XML Reports Consolidator
 
Este script consolida múltiples archivos XML de reportes JUnit en un solo archivo,
manteniendo la estructura XML válida y agregando los contadores de tests.
 
Uso:
    python junit_consolidator.py <directorio_junit> [archivo_salida]
 
Ejemplo:
    python junit_consolidator.py target/surefire-reports/junitreports/
    python junit_consolidator.py target/surefire-reports/junitreports/ consolidated-report.xml
"""
 
import xml.etree.ElementTree as ET
import os
import sys
import glob
from xml.dom import minidom
from typing import List, Tuple
 
 
class JUnitConsolidator:
    """
    Consolidador de reportes JUnit XML.
    
    Esta clase toma múltiples archivos XML de JUnit y los combina en un solo archivo
    manteniendo la estructura correcta de testsuites y agregando los contadores globales.
    """
    
    def __init__(self, junit_directory: str, output_filename: str = "consolidated-junit-report.xml"):
        """
        Inicializar el consolidador.
        
        Args:
            junit_directory: Directorio que contiene los archivos XML de JUnit
            output_filename: Nombre del archivo de salida consolidado
        """
        self.junit_directory = junit_directory
        self.output_filename = output_filename
        self.output_path = os.path.join(junit_directory, output_filename)
        
        # Contadores globales
        self.total_tests = 0
        self.total_failures = 0
        self.total_errors = 0
        self.total_skipped = 0
        self.total_time = 0.0
        
        # Estadísticas de procesamiento
        self.processed_files = 0
        self.failed_files = 0
        self.xml_files = []
    
    def find_xml_files(self) -> List[str]:
        """
        Buscar todos los archivos XML en el directorio especificado.
        
        Returns:
            Lista de rutas a archivos XML encontrados
        """
        if not os.path.exists(self.junit_directory):
            print(f"⚠️ Error: Directorio {self.junit_directory} no existe")
            return []
        
        # Buscar archivos XML, excluyendo el archivo de salida
        pattern = os.path.join(self.junit_directory, "*.xml")
        all_xml_files = glob.glob(pattern)
        
        # Filtrar el archivo de salida si ya existe
        self.xml_files = [
            f for f in all_xml_files
            if os.path.basename(f) != self.output_filename
        ]
        
        return self.xml_files
    
    def parse_testsuite_element(self, testsuite_element: ET.Element) -> Tuple[int, int, int, int, float]:
        """
        Extraer contadores de un elemento testsuite.
        
        Args:
            testsuite_element: Elemento XML testsuite
            
        Returns:
            Tupla con (tests, failures, errors, skipped, time)
        """
        tests = int(testsuite_element.get('tests', 0))
        failures = int(testsuite_element.get('failures', 0))
        errors = int(testsuite_element.get('errors', 0))
        skipped = int(testsuite_element.get('skipped', 0))
        time = float(testsuite_element.get('time', 0))
        
        return tests, failures, errors, skipped, time
    
    def process_xml_file(self, file_path: str, root_element: ET.Element) -> bool:
        """
        Procesar un archivo XML individual y añadirlo al elemento raíz consolidado.
        
        Args:
            file_path: Ruta del archivo XML a procesar
            root_element: Elemento raíz donde añadir las testsuites
            
        Returns:
            True si el procesamiento fue exitoso, False en caso contrario
        """
        try:
            tree = ET.parse(file_path)
            xml_root = tree.getroot()
            file_name = os.path.basename(file_path)
            
            print(f"   📄 Procesando: {file_name}")
            
            # Manejar elemento testsuite individual
            if xml_root.tag == 'testsuite':
                tests, failures, errors, skipped, time = self.parse_testsuite_element(xml_root)
                
                # Actualizar contadores globales
                self.total_tests += tests
                self.total_failures += failures
                self.total_errors += errors
                self.total_skipped += skipped
                self.total_time += time
                
                # Añadir testsuite al root consolidado
                root_element.append(xml_root)
                
            # Manejar elemento testsuites múltiple
            elif xml_root.tag == 'testsuites':
                for testsuite in xml_root.findall('testsuite'):
                    tests, failures, errors, skipped, time = self.parse_testsuite_element(testsuite)
                    
                    # Actualizar contadores globales
                    self.total_tests += tests
                    self.total_failures += failures
                    self.total_errors += errors
                    self.total_skipped += skipped
                    self.total_time += time
                    
                    # Añadir testsuite al root consolidado
                    root_element.append(testsuite)
            else:
                print(f"   ⚠️ Elemento raíz desconocido en {file_name}: {xml_root.tag}")
                return False
                
            return True
            
        except ET.ParseError as e:
            print(f"   ❌ Error de XML en {os.path.basename(file_path)}: {e}")
            self.failed_files += 1
            return False
        except Exception as e:
            print(f"   ❌ Error procesando {os.path.basename(file_path)}: {e}")
            self.failed_files += 1
            return False
    
    def create_consolidated_xml(self, root_element: ET.Element) -> str:
        """
        Crear el contenido XML consolidado con formato bonito.
        
        Args:
            root_element: Elemento raíz con todas las testsuites
            
        Returns:
            String con el XML formateado
        """
        # Establecer atributos del elemento raíz testsuites consolidado
        root_element.set('tests', str(self.total_tests))
        root_element.set('failures', str(self.total_failures))
        root_element.set('errors', str(self.total_errors))
        root_element.set('skipped', str(self.total_skipped))
        root_element.set('time', f'{self.total_time:.3f}')
        
        # Añadir timestamp si no existe
        if not root_element.get('timestamp'):
            from datetime import datetime
            root_element.set('timestamp', datetime.now().isoformat())
        
        # Generar XML con formato bonito
        rough_string = ET.tostring(root_element, 'utf-8')
        reparsed = minidom.parseString(rough_string)
        pretty_xml = reparsed.toprettyxml(indent='  ')
        
        # Limpiar líneas vacías del XML generado
        lines = [line for line in pretty_xml.split('\n') if line.strip()]
        return '\n'.join(lines)
    
    def consolidate(self) -> bool:
        """
        Ejecutar la consolidación completa de reportes JUnit.
        
        Returns:
            True si la consolidación fue exitosa, False en caso contrario
        """
        print("📋 Consolidando reportes JUnit XML...")
        
        # Buscar archivos XML
        xml_files = self.find_xml_files()
        
        if not xml_files:
            print(f"⚠️ No se encontraron archivos XML en {self.junit_directory}")
            return False
        
        print(f"📄 Archivos XML encontrados: {len(xml_files)}")
        for xml_file in xml_files:
            print(f"   • {os.path.basename(xml_file)}")
        
        # Crear elemento raíz para testsuites consolidadas
        root = ET.Element('testsuites')
        
        # Procesar cada archivo XML
        print(f"🔄 Consolidando {len(xml_files)} archivos XML...")
        for xml_file in xml_files:
            if self.process_xml_file(xml_file, root):
                self.processed_files += 1
        
        # Verificar que se procesó al menos un archivo
        if self.processed_files == 0:
            print("❌ No se pudo procesar ningún archivo XML")
            return False
        
        # Generar contenido XML consolidado
        try:
            pretty_xml = self.create_consolidated_xml(root)
            
            # Escribir archivo consolidado
            with open(self.output_path, 'w', encoding='utf-8') as f:
                f.write(pretty_xml)
                
            print(f"✅ Archivo consolidado creado: {os.path.basename(self.output_path)}")
            self.print_summary()
            
            return True
            
        except Exception as e:
            print(f"❌ Error creando archivo consolidado: {e}")
            return False
    
    def print_summary(self):
        """Imprimir resumen de la consolidación."""
        print(f"📊 Resumen de consolidación:")
        print(f"   • Archivos procesados: {self.processed_files}/{len(self.xml_files)}")
        if self.failed_files > 0:
            print(f"   • Archivos con error: {self.failed_files}")
        print(f"   • Tests: {self.total_tests}")
        print(f"   • Failures: {self.total_failures}")
        print(f"   • Errors: {self.total_errors}")
        print(f"   • Skipped: {self.total_skipped}")
        print(f"   • Time: {self.total_time:.3f}s")
        
        # Calcular tasa de éxito
        if self.total_tests > 0:
            success_rate = ((self.total_tests - self.total_failures - self.total_errors) / self.total_tests) * 100
            print(f"   • Tasa de éxito: {success_rate:.1f}%")
 
 
def main():
    """Función principal para uso como script independiente."""
    if len(sys.argv) < 2:
        print("Uso: python junit_consolidator.py <directorio_junit> [archivo_salida]")
        print("\nEjemplos:")
        print("  python junit_consolidator.py target/surefire-reports/junitreports/")
        print("  python junit_consolidator.py target/surefire-reports/junitreports/ my-report.xml")
        sys.exit(1)
    
    junit_directory = sys.argv[1]
    output_filename = sys.argv[2] if len(sys.argv) > 2 else "consolidated-junit-report.xml"
    
    # Crear y ejecutar consolidador
    consolidator = JUnitConsolidator(junit_directory, output_filename)
    success = consolidator.consolidate()
    
    if success:
        print(f"\n📁 Archivos en {junit_directory}:")
        try:
            xml_files = glob.glob(os.path.join(junit_directory, "*.xml"))
            for xml_file in sorted(xml_files):
                file_size = os.path.getsize(xml_file)
                print(f"   {os.path.basename(xml_file)} ({file_size} bytes)")
        except Exception:
            print("   Error listando archivos")
        
        sys.exit(0)
    else:
        print("❌ Error: No se pudo completar la consolidación")
        sys.exit(1)
 
 
if __name__ == "__main__":
    main()