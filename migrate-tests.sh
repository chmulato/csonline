#!/bin/bash

# Script para migrar testes automaticamente
# Substitui JPAUtil por TestJPAUtil em todos os arquivos de teste

echo "🔄 Iniciando migração automática dos testes..."

# Diretório dos testes
TEST_DIR="src/test/java"

# 1. Substituir imports
echo "📦 Substituindo imports..."
find $TEST_DIR -name "*.java" -type f -exec sed -i 's/import com\.caracore\.cso\.repository\.JPAUtil;/import com.caracore.cso.repository.TestJPAUtil;/g' {} \;

# 2. Substituir chamadas do JPAUtil
echo "🔧 Substituindo chamadas JPAUtil.getEntityManager()..."
find $TEST_DIR -name "*.java" -type f -exec sed -i 's/JPAUtil\.getEntityManager()/TestJPAUtil.getEntityManager()/g' {} \;

# 3. Substituir referências diretas
echo "🔄 Substituindo outras referências JPAUtil..."
find $TEST_DIR -name "*.java" -type f -exec sed -i 's/com\.caracore\.cso\.repository\.JPAUtil/com.caracore.cso.repository.TestJPAUtil/g' {} \;

echo "✅ Migração concluída!"
echo "📋 Próximos passos manuais:"
echo "   1. Adicionar 'extends DatabaseTestBase' nas classes de teste que precisam"
echo "   2. Verificar e corrigir imports se necessário"
echo "   3. Executar mvn test para validar"
